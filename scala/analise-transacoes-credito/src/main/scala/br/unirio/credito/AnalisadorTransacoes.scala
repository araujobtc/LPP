import java.time.Month

sealed trait Categoria
case object Alimentacao extends Categoria
case object Transporte extends Categoria
case object Lazer extends Categoria
case object Saude extends Categoria
case object Outros extends Categoria

case class Transacao(
  id: String,
  valor: Double,
  categoria: Categoria,
  hora: Int,
  mes: Month
)

case class RelatorioCliente(
  gastoTotal: Double,
  gastoMedio: Double,
  categoriaFavorita: Categoria,
  horarioDePico: Int,
  scoreRisco: Double,
  limiteRecomendado: Double,
  segmento: String,
  recomendacoes: List[String]
)

object Calculos {
  def gastoTotal(ts: List[Transacao]): Double =
    ts.foldLeft(0.0)((acc, t) => acc + t.valor)

  def gastoMedio(ts: List[Transacao]): Double =
    if (ts.isEmpty) 0.0 else gastoTotal(ts) / ts.size

  def desvioPadrao(valores: List[Double]): Double =
    if (valores.size < 2) 0.0
    else {
      val media = valores.sum / valores.size
      val variancia = valores.map(v => math.pow(v - media, 2)).sum / valores.size
      math.sqrt(variancia)
    }

  def categoriaFavorita(ts: List[Transacao]): Categoria =
    ts.groupBy(_.categoria)
      .view
      .mapValues(_.map(_.valor).sum)
      .maxBy(_._2)._1

  def horarioDePico(ts: List[Transacao]): Int =
    ts.groupBy(_.hora)
      .maxBy(_._2.size)._1

  def transacoesAnomalas(ts: List[Transacao]): List[Transacao] = {
    val valores = ts.map(_.valor)
    val media = valores.sum / valores.size
    val limiar = media + 3 * desvioPadrao(valores)

    ts.filter(_.valor > limiar)
  }

  def scoreRisco(ts: List[Transacao]): Double = {
    val total = gastoTotal(ts)
    val media = gastoMedio(ts)
    val desvio = desvioPadrao(ts.map(_.valor))

    val fatorVariabilidade =
      if (media > 0) math.min(desvio / media, 3.0) else 0.0

    val gastoLazer = ts.filter(_.categoria == Lazer).map(_.valor).sum

    val fatorLazer =
      if (total > 0) (gastoLazer / total) * 5.0 else 0.0

    val fatorAnomalias =
      math.min(ts.count(_.valor > media * 3) * 0.5, 3.0)

    math.min((fatorVariabilidade + fatorLazer + fatorAnomalias) / 3.0 * 10.0, 10.0)
  }
}

object Recomendacoes {
  def limiteRecomendado(gastoMedio: Double, score: Double): Double = {
    val multiplicador = score match {
      case s if s < 3.0 => 2.5
      case s if s < 6.0 => 1.8
      case s if s < 8.0 => 1.3
      case _ => 1.0
    }

    math.round(gastoMedio * multiplicador).toDouble
  }

  def segmento(gastoTotal: Double, score: Double): String =
    (gastoTotal, score) match {
      case (g, s) if g > 10000 && s < 5.0 => "Premium"
      case (g, _) if g > 5000 => "Potencial"
      case (_, s) if s > 7.0 => "Atencao"
      case _ => "Standard"
    }

  def paraOperadora(ts: List[Transacao], score: Double): List[String] = {
    val total = Calculos.gastoTotal(ts)
    val media = Calculos.gastoMedio(ts)
    val anomal = Calculos.transacoesAnomalas(ts)

    List(
      Option.when(score > 7.0)(
        "Risco crítico: reduza o limite ou exija comprovação de renda."
      ),
      Option.when(score > 4.0 && score <= 7.0)(
        "Risco moderado: monitore os próximos 30 dias antes de ampliar limite."
      ),
      Option.when(score < 3.0)(
        "Baixo risco: candidato a programa de fidelidade e cashback."
      ),
      Option.when(anomal.nonEmpty)(
        s"${anomal.size} transação(ões) anômala(s): acione análise antifraude."
      ),
      Option.when(
        ts.exists(_.categoria == Lazer) &&
        Calculos.gastoTotal(ts.filter(_.categoria == Lazer)) / total > 0.4
      )(
        "Lazer acima de 40%: ofereça parcelamento sem juros para fidelizar."
      ),
      Option.when(media > 500)(
        "Alto ticket médio: sugira cartão premium com benefícios em viagens."
      )
    ).flatten
  }
}

object AnalisadorTransacoes {
  def analisarCliente(ts: List[Transacao]): Option[RelatorioCliente] =
    Option.when(ts.nonEmpty) {
      val total = Calculos.gastoTotal(ts)
      val media = Calculos.gastoMedio(ts)
      val catFav = Calculos.categoriaFavorita(ts)
      val horaPico = Calculos.horarioDePico(ts)
      val score = Calculos.scoreRisco(ts)
      val limite = Recomendacoes.limiteRecomendado(media, score)
      val seg = Recomendacoes.segmento(total, score)
      val recs = Recomendacoes.paraOperadora(ts, score)

      RelatorioCliente(total, media, catFav, horaPico, score, limite, seg, recs)
    }

  def analisarTodos(ts: List[Transacao]): Map[String, RelatorioCliente] =
    ts.groupBy(_.id)
      .view
      .mapValues(analisarCliente)
      .collect { case (id, Some(r)) => id -> r }
      .toMap

  def main(args: Array[String]): Unit = {
    val transacoes: List[Transacao] = List(
      Transacao("C001", 120.0, Alimentacao, 12, Month.JANUARY),
      Transacao("C001", 3500.0, Lazer, 21, Month.JANUARY),
      Transacao("C001", 85.0, Transporte, 8, Month.FEBRUARY),
      Transacao("C001", 2800.0, Lazer, 20, Month.FEBRUARY),
      Transacao("C001", 95.0, Saude, 10, Month.MARCH),
      Transacao("C001", 210.0, Alimentacao, 13, Month.MARCH),
      Transacao("C001", 4200.0, Outros, 22, Month.MARCH)
    )

    analisarCliente(transacoes).fold(
      println("Nenhuma transação encontrada.")
    ) { r =>
      println("=" * 55)
      println(" RELATÓRIO DE ANÁLISE — CLIENTE C001")
      println("=" * 55)
      println(f"Gasto total: R$$${r.gastoTotal}%,.2f")
      println(f"Gasto médio: R$$${r.gastoMedio}%,.2f")
      println(s"Categoria favorita: ${r.categoriaFavorita}")
      println(s"Horário de pico: ${r.horarioDePico}h")
      println(f"Score de risco: ${r.scoreRisco}%.1f / 10.0")
      println(f"Limite recomendado: R$$${r.limiteRecomendado}%,.2f")
      println(s"Segmento: ${r.segmento}")
      println("-" * 55)
      println("Recomendações para a operadora:")
      r.recomendacoes.map(rec => s" - $rec").foreach(println)
      println("=" * 55)
    }
  }
}