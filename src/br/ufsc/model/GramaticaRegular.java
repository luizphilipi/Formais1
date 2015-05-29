package br.ufsc.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GramaticaRegular {

	private final Map<String, Set<String>> producoes = new HashMap<String, Set<String>>();
	private String estadoInicial;

	public void setEstadoInicial(String estadoInicial) {
		this.estadoInicial = estadoInicial;
	}

	public boolean adicionarDerivacao(String estado, String derivacao) {
		if ((derivacao.length() == 1 && !terminalPermitido(derivacao.charAt(0))) || (derivacao.length() == 2 && !naoTerminalPermitido(derivacao.charAt(1)))
				|| (derivacao.length() < 0) || (derivacao.length() > 2)) {
			return false;
		}

		Set<String> producao = producoes.get(estado);
		if (producao == null) {
			producao = new HashSet<String>();
			producoes.put(estado, producao);
		}
		producao.add(derivacao);
		return true;
	}

	private boolean naoTerminalPermitido(char c) {
		return Character.isUpperCase(c) || (c == '#');
	}

	private boolean terminalPermitido(char c) {
		return Character.isLowerCase(c) || Character.isDigit(c) || c == '&';
	}

	public Automato gerarAutomato() {
		Automato automato = new Automato();
		automato.addEstadoFinal("Final");
		for (Entry<String, Set<String>> entrada : producoes.entrySet()) {
			automato.addEstado(entrada.getKey());
			for (String derivacao : entrada.getValue()) {
				try {
					if (derivacao.length() == 1) {
						automato.addTransicao(entrada.getKey(), derivacao, "Final");
					} else {
						String proximoEstado = derivacao.charAt(1) + "";
						automato.addEstado(proximoEstado);
						automato.addTransicao(entrada.getKey(), derivacao.charAt(0) + "", proximoEstado);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			automato.setEstadoInicial(estadoInicial);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return automato;

	}

	public String toString() {
		String valor = estadoInicial + " -> ";
		String sep = "";
		for (String derivacao : producoes.get(estadoInicial)) {
			valor += sep + derivacao;
			sep = "|";
		}
		valor += "\n";
		for (Entry<String, Set<String>> entrada : producoes.entrySet()) {
			if (!estadoInicial.equals(entrada.getKey())) {
				valor += entrada.getKey() + " -> ";
				sep = "";
				for (String derivacao : entrada.getValue()) {
					valor += sep + derivacao;
					sep = "|";
				}
				valor += "\n";
			}
		}
		return valor;
	}
}
