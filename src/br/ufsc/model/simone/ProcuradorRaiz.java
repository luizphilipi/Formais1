package br.ufsc.model.simone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Classe responsável por procurar o operador de menor prioridade para utilizar como raiz da arvore costurada
 *
 * @author decker
 */
public class ProcuradorRaiz {

	private static final Map<Character, Integer> mapaPrecendencia = new HashMap<>();

	// Inicia hash de precedencia com a procedencia padrão
	public ProcuradorRaiz() {
		mapaPrecendencia.put('*', 1);
		mapaPrecendencia.put('?', 1);
		mapaPrecendencia.put('.', 2);
		mapaPrecendencia.put('|', 3);
	}

	public int obterPosicaoRaiz(String regex) {
		// Lista dos operadores encontrados
		// Operador é um container de dados definido como uma inner class
		// Saudades struct :'(
		List<Operador> operadores = new ArrayList<>();
		montarOperadores(regex, operadores);
		return menorPrecedencia(operadores);

	}

	public Boolean ehOperador(Character operador) {
		Character[] validos = { '.', '|', '*', '?' };
		for (Character valido : validos) {
			if (Character.compare(valido, operador) == 0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	// Monta a lista de operadores (sem alteração por parênteses)
	private void montarOperadores(String regex, List<Operador> operadores) {
		// Pilha com os parenteses encontrados
		Stack<Character> parenteses = new Stack<>();
		for (int i = 0; i < regex.length(); i++) {
			Character c = regex.charAt(i);
			if (parenteses.empty() && ehOperador(c)) {
				operadores.add(new Operador(c, i));
			} else if (Character.compare(c, '(') == 0) {
				parenteses.push('(');
			} else if (Character.compare(c, ')') == 0) {
				parenteses.pop();
			}
		}
	}

	// retorna a posicao de menor precedencia
	private Integer menorPrecedencia(List<Operador> operadores) {
		Integer menorPrioridade = 0;
		for (int i = 0; i < operadores.size(); i++) {
			if (compararPrecedencia(operadores.get(menorPrioridade).simbolo, operadores.get(i).simbolo)) {
				menorPrioridade = i;
			}
		}
		return operadores.get(menorPrioridade).posicao;
	}

	private Boolean compararPrecedencia(Character primeiro, Character segundo) {
		return (mapaPrecendencia.get(primeiro.charValue()) < mapaPrecendencia.get(segundo.charValue()));
	}

	class Operador {

		// simbolo do operador
		public Character simbolo;
		// posicao do símbolo na regex
		public Integer posicao;

		// Construtor padrão do Operador. Não existe operador sem simbolo e posicao.
		public Operador(Character simbolo, Integer posicao) {
			this.simbolo = simbolo;
			this.posicao = posicao;
		}
	};// Operador
}// Procurador
