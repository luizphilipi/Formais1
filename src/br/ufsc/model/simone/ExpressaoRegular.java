package br.ufsc.model.simone;

import java.util.Stack;

import br.ufsc.model.Automato;

/**
 *
 * @author luiz
 */
public class ExpressaoRegular {

	/**
	 * Informa se o caracter informado pode ser usado em uma expressao regular
	 *
	 * @param c
	 * @return
	 */
	private static boolean charValido(char c) {
		return c == '(' || c == ')' || charOperacoes(c) || charTerminal(c);
	}

	private static boolean charTerminal(char c) {
		return Character.isLetterOrDigit(c);
	}

	private static boolean charOperacoes(char c) {
		return c == '*' || c == '?' || c == '|';
	}

	/**
	 * Verifica se a expressao passada por parâmetro é válida.
	 *
	 * @return
	 */
	public static boolean valida(String regex) {

		if (regex != null) {

			if (regex.length() == 0) {
				return false;
			}

			// se possui apenas um char, entao deve ser um char terminal
			if (regex.length() == 1) {
				return charTerminal(regex.charAt(0));
			}

			Stack<Character> parenteses = new Stack<Character>();
			Stack<Character> regexCompleta = new Stack<Character>();

			// se possui mais de um char, então precisa avaliar a estrutura
			for (int i = 0; i < regex.length(); i++) {
				char c = regex.charAt(i);

				if (charValido(c)) {
					if (c == '(') // adiciona na pilha de parentisação
					{
						parenteses.push(c);
					} else if (c == ')') {
						try {
							char t = parenteses.pop(); // tenta remover da pilha

							t = regexCompleta.peek();

							if (t == '(') // nao pode abrir e fechar parenteses em sequencia
							{
								return false;
							}
						} catch (Exception e) {
							// se não tinha elemento para remover da pilha,
							// então os parenteses estão em ordem incorreta
							return false;
						}

					} else if (charOperacoes(c)) {
						try {
							char t = regexCompleta.peek();

							if (t != ')' && (!charTerminal(t))) {
								return false;
							}

						} catch (Exception e) {
							return false;
						}
					}

					regexCompleta.push(c);

				} else {
					return false;
				}
			}

			if (parenteses.size() > 0) // se algum parenteses ficou sem seu correspondente, então falha.
			{
				return false;
			}

			if (regexCompleta.peek() == '|') // não pode terminar com OU
			{
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	private static boolean possuiApenasTerminais(String s) {
		for (char c : s.toCharArray())
			if (!charTerminal(c))
				return false;
		return true;
	}

	public static Automato obterAutomatoDaExpressaoRegular(String regex) throws IllegalArgumentException {
		if (valida(regex)) {
			Automato automato = obterAutomatoDaExpressaoRegularInterno(regex);

			return automato.obterAutomatoMinimo();
			// return automato;
		} else {
			throw new IllegalArgumentException("Regex inválido");
		}
	}

	private static Automato obterAutomatoDaExpressaoRegularInterno(String regex) {
		if (regex != null) {

			if (regex.length() > 1) {

				String subExpressao = "";
				int parenteses = 0;
				int i = 0;

				do {
					char c = regex.charAt(i++);

					subExpressao = subExpressao.concat(c + "");

					if (c == '(')
						parenteses++;
					else if (c == ')')
						parenteses--;

				} while (parenteses > 0);

				// se os proximos itens forem apenas concatenação, verifica até onde pode usar na mesma concatenacao
				// mas so faz isso se depois da concatenação houver outra operacao
				if (subExpressao.length() == 1 && !possuiApenasTerminais(regex)) {

					while (i < regex.length() && charTerminal(regex.charAt(i)))
						subExpressao = subExpressao.concat(regex.charAt(i++) + "");

					// porém se pegou um caracter que está junto com um ? ou *,
					// estão precisa removê-lo, pois ele não faz parte da concatenação.
					if (subExpressao.length() > 1 && i < regex.length() && (regex.charAt(i) == '*' || regex.charAt(i) == '?'))
						subExpressao = subExpressao.substring(0, subExpressao.length() - 1); // remove o último terminal lido

				}

				// removo da regex a subexpressao encontrada
				regex = regex.substring(subExpressao.length());

				// se a subexpressao começa com parenteses, entao retiro
				if (subExpressao.charAt(0) == '(')
					subExpressao = subExpressao.substring(1, subExpressao.length() - 1);

				// gero o automato para a subexpressao encontrada
				Automato inicio = obterAutomatoDaExpressaoRegularInterno(subExpressao);

				// verifico qual a próxima operacao do que sobrou da regex
				Automato restante = null;
				int operacao = 1; // 1=concatenar, 2 = unificar
				if (regex.length() > 0) {
					char c = regex.charAt(0);

					// se for uma operacao, então preciso performá-la com a parte inicial.
					// Caso contrário é uma concatenação
					if (charOperacoes(c)) {
						regex = regex.substring(1);

						switch (c) {
						case '*':
							inicio = inicio.obterAutomatoFechamento();
							break;
						case '|':
							operacao = 2;
							restante = obterAutomatoDaExpressaoRegularInterno(regex);
							break;
						case '?':
							inicio = inicio.obterAutomatoOpcao();
							break;
						}
					} else {
						restante = obterAutomatoDaExpressaoRegularInterno(regex);
					}
				}

				if (regex.length() == 0)
					return inicio;
				else if (restante == null)
					restante = obterAutomatoDaExpressaoRegularInterno(regex);

				if (operacao == 1) {
					if (restante != null)
						return inicio.obterAutomatoConcatenadoCom(restante);
					else
						return inicio;
				} else {
					if (restante != null)
						return inicio.uniao(restante);
					else
						return inicio;
				}

			} else if (regex.length() == 1) {
				// Cria um automato do tipo S->A por a

				Automato simples = new Automato();

				char[] alfabeto = new char[1];
				alfabeto[0] = regex.charAt(0);

				simples.addEstado("S");
				simples.addEstadoFinal("A");

				try {
					simples.setEstadoInicial("S");
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (char d : alfabeto) {
					simples.addSimbolo(d + "");
				}

				try {
					simples.addTransicao("S", alfabeto[0] + "", "A");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return simples;
			}
		}

		return null;
	}
}
