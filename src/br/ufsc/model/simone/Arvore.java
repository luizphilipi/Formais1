package br.ufsc.model.simone;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Arvore costurada, para ser utilizada pelo metodo de DeSimone
 *
 * @author decker
 */
public class Arvore {

	No raiz;
	List<No> folhas;

	// Cria uma arvore para uma dada Regex
	public Arvore(String regex) {
		regex = explicitarConcatenacao(regex);
		raiz = criarRaizes(null, regex);
		// CHUNCHO, mas ajuda na costura
		Stack<No> operadores = new Stack<>();
		operadores.push(new No('&', null));
		costurar(raiz, operadores);
		folhas = listaFolhas();
	}

	// Cria todas as subarvores (catando as raizes) e cospe a raiz geral

	private No criarRaizes(No no, String regex) {
		No novoNo = new No();
		ProcuradorRaiz umaRaiz = new ProcuradorRaiz();
		regex = apararParenteses(regex);
		int raizona = umaRaiz.obterPosicaoRaiz(regex);
		novoNo.simbolo = regex.charAt(raizona);
		novoNo.pai = no;
		String subRegexEsquerda = regex.substring(0, raizona);
		String subRegexDireita = regex.substring(raizona + 1);
		// Construir a galhada da esquerda
		if (subRegexEsquerda.length() > 1) {
			novoNo.filhoEsquerda = criarRaizes(novoNo, subRegexEsquerda);
		} else if (subRegexEsquerda.length() == 1) {
			novoNo.filhoEsquerda = new No(subRegexEsquerda.charAt(0), novoNo);
		}
		if (subRegexDireita.length() > 1) {
			novoNo.filhoDireita = criarRaizes(novoNo, subRegexDireita);
		} else if (subRegexDireita.length() == 1) {
			novoNo.filhoDireita = new No(subRegexDireita.charAt(0), novoNo);
		}

		return novoNo;
	}

	// Realiza as costuras na arvore
	// Prepara o ponto cruz
	private void costurar(No no, Stack<No> operadores) {
		if (no == null) {
			return;
			// WTF JUST HAPPENED?
		}
		if (ehOperador(no.simbolo)) {
			operadores.push(no);
		} else {
			No alce = no;
			do {
				alce.costurado = operadores.pop();
				alce = alce.costurado;
				if (alce == null) {
					break; // Costura pra &.
				}
			} while (ehOperadorUnario(alce.simbolo));
		}
		costurar(no.filhoEsquerda, operadores);
		costurar(no.filhoDireita, operadores);

	}

	// Remove parenteses externos desnecessários
	private String apararParenteses(String regex) {
		String alce = regex;
		if (alce.startsWith("(") && alce.endsWith(")")) {
			// Troca por caracteres intermediarios
			for (int i = 0; i < alce.length() / 2; i++) {
				if (alce.charAt(i) == '(' && alce.charAt(alce.length() - (i + 1)) == ')') {
					alce = "[" + alce.substring(i + 1, alce.length() - (i + 1)) + "]";
				} else {
					break;
				}
			}
			// Pilha dos parenteses
			Stack<Character> parenteses = new Stack<>();
			// Contruir a pilha
			for (int i = 0; i < alce.length(); i++) {
				// se for um "comeco"
				if (alce.charAt(i) == '[') {
					parenteses.push('[');
					// Se for um "fim" e tem "comeco" na pilha
				} else if (alce.charAt(i) == ']' && parenteses.peek() == '[') {
					parenteses.pop();
					// abre parenteses com um "comeco" na pilha
				} else if (alce.charAt(i) == '(' && (parenteses.peek() == '(' || parenteses.peek() == '[')) {
					parenteses.push('(');
					// Fecha parenteses com abre na pilha [)(]
				} else if (alce.charAt(i) == ')' && parenteses.peek() == '(') {
					parenteses.pop();
				}
			}
			// Corrigida?
			if (parenteses.isEmpty()) {
				return alce.substring(1, alce.length() - 1);
			} else {
				// Tava de boa já
				return regex;
			}

		} else {
			// Nao ta na turma das problemáticas :D
			return regex;
		}
	}

	// As concatenações tem que ser explicitadas. ab não é internamente compreendido como a.b
	static public String explicitarConcatenacao(String regex) {
		String alce = "";
		for (int i = 0; i < regex.length() - 1; i++) {
			alce += regex.charAt(i);
			if (ehOutro(regex.charAt(i))) {
				if (ehOutro(regex.charAt(i + 1)) && ehOutro(regex.charAt(i))) {
					alce += ".";

				}
				if (ehOutro(regex.charAt(i)) && regex.charAt(i + 1) == '(') {
					alce += ".";
				}

			}
			if (regex.charAt(i) == '?' && ((ehOutro(regex.charAt(i + 1))) || regex.charAt(i + 1) == '(')) {
				alce += ".";
			}
			if (regex.charAt(i) == '*' && (ehOutro(regex.charAt(i + 1)) || regex.charAt(i + 1) == '(')) {
				alce += ".";
			}
			if (regex.charAt(i) == ')' && regex.charAt(i + 1) == '(') {
				alce += ".";
			}
		}
		alce += regex.charAt(regex.length() - 1);

		return alce;
	}

	// Adiciona as folhas na arvore, em ordem
	private void addFolhaInOrder(No no, List<No> nos, Integer numeroNo) {
		// Pilha de nos
		Stack<No> pilha = new Stack<>();
		while (!pilha.empty() || no != null) {
			if (no != null) {
				pilha.push(no);
				no = no.filhoEsquerda;
			} else {
				no = pilha.pop();
				if (ehOperador(no.simbolo)) {
					no.controle = numeroNo;
					nos.add(no);
					numeroNo++;
				}
				no = no.filhoDireita;
			}
		}
	}

	static public Boolean ehParenteses(Character parenteses) {
		Character[] validos = { '(', ')' };
		for (Character valido : validos) {
			if (Character.compare(valido, parenteses) == 0) {
				return true;
			}
		}
		return false;
	}

	static public Boolean ehOutro(Character outro) {
		return !ehOperador(outro) && !ehParenteses(outro);
	}

	static public Boolean ehOperador(Character operador) {
		Character[] validos = { '.', '|', '*', '?' };
		for (Character valido : validos) {
			if (Character.compare(valido, operador) == 0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public Boolean ehOperadorUnario(Character operador) {
		Character[] validos = { '*', '?' };
		for (Character valido : validos) {
			if (Character.compare(valido, operador) == 0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private List<No> listaFolhas() {
		List<No> foolhas = new ArrayList<>();
		addFolhaInOrder(raiz, foolhas, 1);
		return foolhas;
	}
}
