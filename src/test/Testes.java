package test;

import br.ufsc.model.Automato;

public class Testes {

	public static void main(String[] args) {
		// Automato automato = new Automato();
		//
		// automato.addEstadoFinal("S1");
		// automato.addEstado("S2");
		//
		// automato.addSimbolo("0");
		// automato.addSimbolo("1");
		//
		// try {
		// automato.addTransicao("S1", "0", "S2");
		// automato.addTransicao("S1", "1", "S1");
		// automato.addTransicao("S2", "0", "S1");
		// // automato.addTransicao("S2", "1", "S2");
		// automato.setEstadoInicial("S1");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println(automato);
		// System.out.println(automato.reconhecerSentenca("&"));
		// System.out.println(automato.reconhecerSentenca("100"));
		// System.out.println(automato.reconhecerSentenca("1000"));
		// System.out.println(automato.reconhecerSentenca("1010"));
		// System.out.println(automato.reconhecerSentenca("1001"));
		// System.out.println(automato.reconhecerSentenca("00"));
		// System.out.println(automato.reconhecerSentenca("1"));
		//
		// System.out.println();
		// System.out.println(automato.obterCompleto());
		// System.out.println();
		//
		// System.out.println(automato.obterComplemento());

		// System.out.println();
		// System.out.println("======================== TESTES COM GRAMï¿½TICA ========================");
		// System.out.println();
		//
		// String gramaticaSTR = "S -> a|aS|bB\nB -> b|bS";
		// GramaticaRegular gramatica = new GramaticaRegular();
		// String[] linhas = gramaticaSTR.split("\n");
		// gramatica.setEstadoInicial(linhas[0].split("\\s*->\\s*")[0]);
		// for (String linha : linhas) {
		// String[] linhaSplit = linha.split("\\s*->\\s*");
		// String estado = linhaSplit[0];
		// for (String derivacao : linhaSplit[1].split("\\|")) {
		// gramatica.adicionarDerivacao(estado, derivacao);
		// }
		// }
		// System.out.println(gramatica);
		// Automato automatoNaoDeterministico = gramatica.gerarAutomato();
		// System.out.println(automatoNaoDeterministico);
		//
		// System.out.println();
		// // System.out.println(automatoNaoDeterministico.determinizar());
		//
		// Automato automato2 = new Automato();
		//
		// automato2.addEstado("Q0");
		// automato2.addEstado("Q1");
		// automato2.addEstado("Q2");
		// automato2.addEstadoFinal("Q3");
		//
		// automato2.addSimbolo("a");
		// automato2.addSimbolo("b");
		//
		// try {
		// automato2.addTransicao("Q0", "a", "Q0");
		// automato2.addTransicao("Q0", "a", "Q1");
		// automato2.addTransicao("Q0", "b", "Q0");
		// automato2.addTransicao("Q1", "b", "Q2");
		// automato2.addTransicao("Q2", "b", "Q3");
		// automato2.setEstadoInicial("Q0");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println();
		// System.out.println(automato2);
		// System.out.println();
		//
		// System.out.println(automato2.determinizar());
		//
		// Automato automato3 = new Automato();
		//
		// automato3.addEstado("Q0");
		// automato3.addEstado("Q1");
		// automato3.addEstado("Q2");
		// automato3.addEstadoFinal("Q3");
		//
		// automato3.addSimbolo("0");
		// automato3.addSimbolo("1");
		//
		// try {
		// automato3.addTransicao("Q0", "0", "Q0");
		// automato3.addTransicao("Q0", "0", "Q1");
		// automato3.addTransicao("Q0", "1", "Q0");
		// automato3.addTransicao("Q0", "1", "Q2");
		// automato3.addTransicao("Q1", "0", "Q3");
		// automato3.addTransicao("Q2", "1", "Q3");
		// automato3.addTransicao("Q3", "0", "Q3");
		// automato3.addTransicao("Q3", "1", "Q3");
		// automato3.setEstadoInicial("Q0");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println();
		// System.out.println(automato3);
		// System.out.println();
		//
		// System.out.println(automato3.determinizar());

		// Automato imparDivisivelPor3 = criaImparDivisivelPor3();
		// System.out.println();
		// System.out.println(imparDivisivelPor3.removerEpsilonTransicoes());
		// System.out.println();
		// System.out.println(imparDivisivelPor3.obterEstadosAlcancaveis());
		// System.out.println(imparDivisivelPor3.obterEstadosVivos());

		// Automato aEstrela = aEstrela();
		// System.out.println("a*\n" + aEstrela + "\n");
		// System.out.println("complemento a*\n" + aEstrela.obterComplemento() + "\n");
		// Automato bEstrela = bEstrela();
		// System.out.println("b*\n" + bEstrela + "\n");
		// System.out.println("complemento b*\n" + bEstrela.obterComplemento() + "\n");
		// Automato uniao = aEstrela.uniao(bEstrela);
		// System.out.println("União\n" + uniao + "\n");
		// System.out.println(uniao.reconhecerSentenca("aaaa"));
		// System.out.println(uniao.reconhecerSentenca("bbbb"));
		//
		// System.out.println();
		// Automato interseccao = aEstrela.interseccao(bEstrela);
		// System.out.println("Interseccao\n" + interseccao + "\n");
		// System.out.println();
		// System.out.println(interseccao.reconhecerSentenca("b"));

		// Automato automatoNathalia = automatoNathalia();
		// System.out.println(automatoNathalia.obterAutomatoMinimo());

		// Automato exercicio4a = exercicio4a();
		// System.out.println(exercicio4a.determinizar());
		// System.out.println();
		// System.out.println(exercicio4a.obterAutomatoMinimo());

		System.out.println(exercicio10().reconhecerSentenca("ababab"));
	}

	public static Automato criaImparDivisivelPor3() {
		Automato automato = new Automato();
		automato.addEstado("P");
		automato.addEstadoFinal("Q");
		automato.addEstadoFinal("R");
		automato.addEstado("Q1");
		automato.addEstado("R1");
		automato.addEstado("R2");

		automato.addSimbolo("&");
		automato.addSimbolo("a");

		try {
			automato.addTransicao("P", "&", "Q");
			automato.addTransicao("P", "&", "R");
			automato.addTransicao("Q", "a", "Q1");
			automato.addTransicao("Q1", "a", "Q");
			automato.addTransicao("R", "a", "R1");
			automato.addTransicao("R1", "a", "R2");
			automato.addTransicao("R2", "a", "R");
			automato.setEstadoInicial("P");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(automato);
		return automato;
	}

	public static Automato aEstrela() {
		Automato automato = new Automato();

		automato.addEstadoFinal("Q");

		try {
			automato.addTransicao("Q", "a", "Q");
			automato.setEstadoInicial("Q");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return automato;
	}

	public static Automato bEstrela() {
		Automato automato = new Automato();

		automato.addEstadoFinal("Q");

		try {
			automato.addTransicao("Q", "b", "Q");
			automato.setEstadoInicial("Q");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return automato;
	}

	public static Automato automatoNathalia() {
		Automato automato = new Automato();

		automato.addEstado("S");
		automato.addEstadoFinal("A");
		automato.addEstado("C");
		automato.addEstadoFinal("D");

		automato.addSimbolo("a");
		automato.addSimbolo("b");
		automato.addSimbolo("c");
		automato.addSimbolo("d");

		try {
			automato.addTransicao("S", "a", "S");
			automato.addTransicao("S", "a", "C");
			automato.addTransicao("S", "b", "A");
			automato.addTransicao("S", "c", "D");
			automato.addTransicao("S", "d", "C");
			automato.addTransicao("A", "a", "A");
			automato.addTransicao("A", "a", "D");
			automato.addTransicao("A", "b", "S");
			automato.addTransicao("A", "d", "D");
			automato.addTransicao("C", "c", "D");
			automato.addTransicao("C", "d", "C");
			automato.addTransicao("D", "c", "C");
			automato.addTransicao("D", "d", "D");
			automato.setEstadoInicial("S");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return automato;
	}

	public static Automato exercicio4a() {
		Automato automato = new Automato();

		automato.addEstadoFinal("S");
		automato.addEstado("A");
		automato.addEstado("B");
		automato.addEstado("C");
		automato.addEstado("D");
		automato.addEstadoFinal("F");

		automato.addSimbolo("a");
		automato.addSimbolo("b");

		try {
			automato.addTransicao("S", "a", "B");
			automato.addTransicao("S", "a", "D");
			automato.addTransicao("S", "a", "F");
			automato.addTransicao("S", "b", "A");
			automato.addTransicao("S", "b", "C");
			automato.addTransicao("S", "b", "F");
			automato.addTransicao("A", "a", "B");
			automato.addTransicao("A", "a", "F");
			automato.addTransicao("A", "b", "A");
			automato.addTransicao("B", "a", "A");
			automato.addTransicao("B", "b", "B");
			automato.addTransicao("B", "b", "F");
			automato.addTransicao("C", "a", "D");
			automato.addTransicao("C", "b", "C");
			automato.addTransicao("C", "b", "F");
			automato.addTransicao("D", "a", "C");
			automato.addTransicao("D", "a", "F");
			automato.addTransicao("D", "b", "D");
			automato.setEstadoInicial("S");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return automato;
	}

	public static Automato exercicio10() {
		Automato automato1 = new Automato();

		automato1.addEstado("A");
		automato1.addEstadoFinal("B");
		automato1.addEstado("C");
		automato1.addEstadoFinal("D");

		automato1.addSimbolo("a");
		automato1.addSimbolo("b");

		try {
			automato1.addTransicao("A", "a", "B");
			automato1.addTransicao("A", "b", "C");
			automato1.addTransicao("B", "a", "A");
			automato1.addTransicao("B", "b", "D");
			automato1.addTransicao("C", "a", "B");
			automato1.addTransicao("D", "a", "A");
			automato1.setEstadoInicial("A");
		} catch (Exception e) {
			e.printStackTrace();
		}

		automato1.toString();
		automato1.obterComplemento().toString();

		Automato automato2 = new Automato();

		automato2.addEstado("A");
		automato2.addEstadoFinal("B");

		automato2.addSimbolo("a");
		automato2.addSimbolo("b");

		try {
			automato2.addTransicao("A", "a", "A");
			automato2.addTransicao("A", "b", "B");
			automato2.addTransicao("B", "a", "B");
			automato2.addTransicao("B", "b", "A");
			automato2.setEstadoInicial("A");
		} catch (Exception e) {
			e.printStackTrace();
		}

		automato2.toString();
		automato2.obterComplemento().toString();

		return automato1.interseccao(automato2);
	}
}
