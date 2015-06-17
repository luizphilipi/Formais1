package br.ufsc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class Automato {

	private Set<Estado> estados = new HashSet<Estado>();

	private List<String> alfabeto = new ArrayList<String>();

	private Estado estadoInicial = null;

	public void addEstado(String nome) {
		estados.add(new Estado(nome));
	}

	public void addEstadoFinal(String nome) {
		try {
			encontrarEstado(nome).setTerminal(true);
		} catch (Exception e) {
			estados.add(new Estado(nome, true));
		}
	}

	public void remEstado(String nome) {
		Iterator<Estado> iterator = estados.iterator();
		while (iterator.hasNext()) {
			Estado element = iterator.next();
			if (element.getNome().equals(nome)) {
				iterator.remove();
				break;
			}
		}
	}

	private Estado encontrarEstado(String nome) throws Exception {
		Iterator<Estado> iterator = estados.iterator();
		while (iterator.hasNext()) {
			Estado element = iterator.next();
			if (element.getNome().equals(nome)) {
				return element;
			}
		}
		throw new Exception("Elemento não encontrado");
	}

	public void addTransicao(String de, String simbolo, String para) throws Exception {
		addSimbolo(simbolo);
		Estado estadoDe = encontrarEstado(de);
		Estado estadoPara = encontrarEstado(para);
		estadoDe.addTransicao(simbolo, estadoPara);
	}

	public void addSimbolo(String simbolo) {
		if (!alfabeto.contains(simbolo)) {
			alfabeto.add(simbolo);
		}
	}

	public Estado setEstadoInicial(String nome) throws Exception {
		estadoInicial = encontrarEstado(nome);
		estadoInicial.setInicial(true);
		return estadoInicial;
	}

	public boolean reconhecerSentenca(String sentenca) {
		return reconhecer(sentenca.replace("&", ""), estadoInicial);
	}

	private boolean reconhecer(String sentenca, Estado estadoAtual) {
		if (sentenca.length() == 0) {
			return estadoAtual.isTerminal();
		}
		String simbolo = sentenca.charAt(0) + "";
		if (estadoAtual.transitar(simbolo) != null) {
			for (Estado proximoEstado : estadoAtual.transitar(simbolo)) {
				if (reconhecer(sentenca.substring(1), proximoEstado)) {
					return true;
				}
			}
		}
		if (estadoAtual.transitar("&") != null) {
			for (Estado proximoEstado : estadoAtual.transitar("&")) {
				if (reconhecer(sentenca, proximoEstado)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDeterministico() {
		for (Estado estado : estados) {
			if (!estado.isDeterministico()) {
				return false;
			}
		}
		return true;
	}

	public boolean possuiEpsilonTransicao() {
		return alfabeto.contains("&");
	}

	public Automato determinizar() {
		Automato automatoDeterministico = new Automato();
		automatoDeterministico.alfabeto = alfabeto;
		List<List<Estado>> novosEstados = new ArrayList<List<Estado>>();
		List<Estado> estado = new ArrayList<Estado>();
		estado.add(estadoInicial);
		novosEstados.add(estado);
		int posicao = 0;
		try {
			while (novosEstados.size() > posicao) {
				estado = novosEstados.get(posicao);
				String nome = "";
				String sep = "";
				boolean terminal = false;
				for (Estado e : estado) {
					nome += sep + e.getNome();
					sep = "|";
					if (e.isTerminal()) {
						terminal = true;
					}
				}
				if (terminal) {
					automatoDeterministico.addEstadoFinal(nome);
				} else {
					automatoDeterministico.addEstado(nome);
				}
				for (String simbolo : alfabeto) {
					List<Estado> estadoDeterminizado = geraEstadoDeterministico(estado, simbolo);
					if (!novosEstados.contains(estadoDeterminizado)) {
						novosEstados.add(estadoDeterminizado);
					}
					String nomeDeterminizado = "";
					sep = "";
					terminal = false;
					for (Estado e : estadoDeterminizado) {
						nomeDeterminizado += sep + e.getNome();
						sep = "|";
						if (e.isTerminal()) {
							terminal = true;
						}
					}
					if (terminal) {
						automatoDeterministico.addEstadoFinal(nomeDeterminizado);
					} else {
						automatoDeterministico.addEstado(nomeDeterminizado);
					}
					automatoDeterministico.addTransicao(nome, simbolo, nomeDeterminizado);
				}
				posicao++;
			}
			automatoDeterministico.setEstadoInicial(this.estadoInicial.getNome());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return automatoDeterministico;
	}

	private List<Estado> geraEstadoDeterministico(List<Estado> estado, String simbolo) {
		Set<Estado> novoEstado = new HashSet<Estado>();

		for (Estado e : estado) {
			if (e.getTransicoes().get(simbolo) != null) {
				novoEstado.addAll(e.getTransicoes().get(simbolo));
			}
		}
		List<Estado> estados = new ArrayList<Estado>(novoEstado);
		Collections.sort(estados);
		return estados;
	}

	public Automato removerEpsilonTransicoes() {
		if (!possuiEpsilonTransicao()) {
			return this;
		}
		Automato novo = new Automato();
		novo.alfabeto = alfabeto;
		novo.alfabeto.remove("&");
		try {
			for (Estado estado : estados) {
				boolean terminal = estado.isTerminal();
				novo.addEstado(estado.getNome());
				for (Entry<String, List<Estado>> transicao : estado.getTransicoes().entrySet()) {
					if (transicao.getKey().equals("&")) {
						for (Estado filho : transicao.getValue()) {
							if (filho.isTerminal()) {
								terminal = true;
							}
							for (Entry<String, List<Estado>> transicaoFilho : filho.getTransicoes().entrySet()) {
								String simbolo = transicaoFilho.getKey();
								for (Estado neto : transicaoFilho.getValue()) {
									if (neto.isTerminal()) {
										novo.addEstadoFinal(neto.getNome());
									} else {
										novo.addEstado(neto.getNome());
									}
									novo.addTransicao(estado.getNome(), simbolo, neto.getNome());
								}
							}
						}
					} else {
						for (Estado filho : transicao.getValue()) {
							if (filho.isTerminal()) {
								novo.addEstadoFinal(filho.getNome());
							} else {
								novo.addEstado(filho.getNome());
							}
							novo.addTransicao(estado.getNome(), transicao.getKey(), filho.getNome());
						}
					}
				}
				novo.encontrarEstado(estado.getNome()).setTerminal(terminal);
			}
			novo.setEstadoInicial(estadoInicial.getNome());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return novo;
	}

	public Set<Estado> obterEstadosAlcancaveis() {
		Set<Estado> alcancaveis = new HashSet<Estado>();
		Stack<Estado> auxiliar = new Stack<Estado>();

		alcancaveis.add(estadoInicial);
		auxiliar.push(estadoInicial);

		while (!auxiliar.empty()) {
			Estado e = auxiliar.pop();

			for (Entry<String, List<Estado>> entry : e.getTransicoes().entrySet()) {
				List<Estado> listaDestinos = entry.getValue();

				Iterator<Estado> it = listaDestinos.iterator();
				while (it.hasNext()) {
					Estado destino = it.next();
					if (!alcancaveis.contains(destino)) {
						alcancaveis.add(destino);
						auxiliar.push(destino);
					}
				}
			}
		}

		return alcancaveis;
	}

	public Set<Estado> obterEstadosVivos() {
		Set<Estado> vivos = new HashSet<Estado>();
		for (Estado estado : estados) {
			if (estado.isTerminal()) {
				vivos.add(estado);
			} else {
				Set<Estado> reentrancia = new HashSet<Estado>();
				Stack<Estado> analisar = new Stack<Estado>();
				reentrancia.add(estado);
				analisar.push(estado);
				boolean vivo = false;

				while (!analisar.empty() && !vivo) {
					Estado e = analisar.pop();
					for (Entry<String, List<Estado>> entrada : e.getTransicoes().entrySet()) {
						for (Estado destino : entrada.getValue()) {
							if (!reentrancia.contains(destino)) {
								reentrancia.add(destino);
								if (vivos.contains(destino) || destino.isTerminal()) {
									vivo = true;
								} else {
									analisar.push(destino);
								}
							}
						}
					}
				}
				if (vivo) {
					vivos.add(estado);
				}
			}
		}
		return vivos;
	}

	public Automato obterCompleto() {
		Automato automato = new Automato();
		automato.alfabeto = alfabeto;
		boolean estadoErro = false;
		try {
			for (Estado estado : estados) {
				if (estado.isTerminal()) {
					automato.addEstadoFinal(estado.getNome());
				} else {
					automato.addEstado(estado.getNome());
				}
				for (String simbolo : alfabeto) {
					List<Estado> transicoes = estado.getTransicoes().get(simbolo);
					if (transicoes == null) {
						automato.addEstado("Erro");
						automato.addTransicao(estado.getNome(), simbolo, "Erro");
						estadoErro = true;
					} else {
						for (Estado estado2 : transicoes) {
							if (estado2.isTerminal()) {
								automato.addEstadoFinal(estado2.getNome());
							} else {
								automato.addEstado(estado2.getNome());
							}
							automato.addTransicao(estado.getNome(), simbolo, estado2.getNome());
						}
					}
				}
			}
			automato.setEstadoInicial(estadoInicial.getNome());
			if (estadoErro) {
				for (String simbolo : alfabeto) {
					automato.addTransicao("Erro", simbolo, "Erro");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return automato;
	}

	public Automato obterComplemento() {
		Automato automato = new Automato();
		automato.alfabeto = alfabeto;
		automato = removerEpsilonTransicoes();
		if (!automato.isDeterministico()) {
			automato = automato.determinizar();
		}
		automato = automato.obterCompleto();

		for (Estado estado : automato.estados) {
			if (estado.isTerminal()) {
				estado.setTerminal(false);
			} else {
				estado.setTerminal(true);
			}
		}

		return automato;
	}

	public Automato uniao(Automato automato) {
		Automato uniao = new Automato();

		try {
			if (estadoInicial.isTerminal() || automato.estadoInicial.isTerminal()) {
				uniao.addEstadoFinal("S");
			} else {
				uniao.addEstado("S");
			}
			uniao.setEstadoInicial("S");
			String sufixo = "1";
			for (Estado estado : estados) {
				if (estado.isTerminal()) {
					uniao.addEstadoFinal(estado.getNome() + sufixo);
				} else {
					uniao.addEstado(estado.getNome() + sufixo);
				}
				for (Entry<String, List<Estado>> entrada : estado.getTransicoes().entrySet()) {
					for (Estado filho : entrada.getValue()) {
						if (filho.isTerminal()) {
							uniao.addEstadoFinal(filho.getNome() + sufixo);
						} else {
							uniao.addEstado(filho.getNome() + sufixo);
						}
						uniao.addTransicao(estado.getNome() + sufixo, entrada.getKey(), filho.getNome() + sufixo);
					}
				}
			}
			uniao.addTransicao("S", "&", estadoInicial.getNome() + sufixo);

			sufixo = "2";
			for (Estado estado : automato.estados) {
				if (estado.isTerminal()) {
					uniao.addEstadoFinal(estado.getNome() + sufixo);
				} else {
					uniao.addEstado(estado.getNome() + sufixo);
				}
				for (Entry<String, List<Estado>> entrada : estado.getTransicoes().entrySet()) {
					for (Estado filho : entrada.getValue()) {
						if (filho.isTerminal()) {
							uniao.addEstadoFinal(filho.getNome() + sufixo);
						} else {
							uniao.addEstado(filho.getNome() + sufixo);
						}
						uniao.addTransicao(estado.getNome() + sufixo, entrada.getKey(), filho.getNome() + sufixo);
					}
				}
			}
			uniao.addTransicao("S", "&", automato.estadoInicial.getNome() + sufixo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return uniao;
	}

	public Automato interseccao(Automato automato) {
		// A1 intersec A2 = (!(!(A1 uniao A2))) = (!(!A1 uniao !A2))
		Set<String> uniaoAlfabeto = new HashSet<String>();
		uniaoAlfabeto.addAll(alfabeto);
		uniaoAlfabeto.addAll(automato.alfabeto);
		alfabeto = new ArrayList<String>(uniaoAlfabeto);
		automato.alfabeto = new ArrayList<String>(uniaoAlfabeto);
		Automato complemento = obterComplemento();
		Automato automatoComplementar = automato.obterComplemento();
		Automato uniao = complemento.uniao(automatoComplementar);
		return uniao.obterComplemento();
	}

	public Automato obterAutomatoMinimo() {
		Automato minimo;

		Automato automatoBase = this;

		if (this.possuiEpsilonTransicao()) {
			automatoBase = automatoBase.removerEpsilonTransicoes();
		}
		if (!this.isDeterministico()) {
			automatoBase = automatoBase.determinizar();
		}

		Set<Estado> alcancaveis = automatoBase.obterEstadosAlcancaveis();
		Set<Estado> vivos = automatoBase.obterEstadosVivos();

		List<Estado> resultantes = new ArrayList<Estado>();

		for (Estado estado : alcancaveis) {
			if (vivos.contains(estado)) {
				resultantes.add(estado);
			}
		}

		Estado inicial = automatoBase.estadoInicial;

		boolean possuiEstadoEquivalente = false;

		do {
			minimo = new Automato();
			possuiEstadoEquivalente = false;

			Map<List<Object>, List<Estado>> teste = new HashMap<List<Object>, List<Estado>>();

			for (Estado estado : resultantes) {
				List<Object> key = new ArrayList<Object>();
				key.add(estado.isTerminal());
				for (String simbolo : alfabeto) {
					key.add(estado.getTransicoes().containsKey(simbolo) ? estado.getTransicoes().get(simbolo).get(0).getNome() : "");
				}
				if (!teste.containsKey(key)) {
					teste.put(key, new ArrayList<Estado>());
				} else {
					possuiEstadoEquivalente = true;
				}
				teste.get(key).add(estado);
			}

			resultantes = new ArrayList<Estado>();

			Map<List<Estado>, String> equivalencia = new HashMap<List<Estado>, String>();
			int i = 0;
			for (Entry<List<Object>, List<Estado>> entrada : teste.entrySet()) {
				String nome = "Q" + i;
				if (entrada.getValue().get(0).isTerminal()) {
					minimo.addEstadoFinal(nome);
				} else {
					minimo.addEstado(nome);
				}
				if (entrada.getValue().contains(inicial)) {
					try {
						inicial = minimo.setEstadoInicial(nome);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				equivalencia.put(entrada.getValue(), nome);
				i++;
			}

			for (Entry<List<Object>, List<Estado>> entrada : teste.entrySet()) {
				String nome = equivalencia.get(entrada.getValue());
				Estado estadoDe = entrada.getValue().get(0);
				for (Entry<String, List<Estado>> transicao : estadoDe.getTransicoes().entrySet()) {
					Estado estadoPara = transicao.getValue().get(0);
					for (Entry<List<Estado>, String> entradaEq : equivalencia.entrySet()) {
						if (entradaEq.getKey().contains(estadoPara)) {
							try {
								minimo.addTransicao(nome, transicao.getKey(), entradaEq.getValue());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			resultantes = new ArrayList<Estado>(minimo.estados);
		} while (possuiEstadoEquivalente);

		return minimo;
	}

	@Override
	public String toString() {
		String leftAlignFormat = "| %-1s%-1s%-13s ";
		for (int i = 0; i < alfabeto.size(); i++) {
			leftAlignFormat += "| %-9s ";
		}
		leftAlignFormat += "|%n";

		String headerFotter = "+-----------------";
		for (int i = 0; i < alfabeto.size(); i++) {
			headerFotter += "+-----------";
		}
		headerFotter += "+%n";
		System.out.format(headerFotter);
		String header = "|     Estado      ";
		for (String simbolo : alfabeto) {
			header += "|     " + simbolo + "     ";
		}
		header += "|%n";
		System.out.printf(header);
		System.out.format(headerFotter);

		for (Estado estado : estados) {
			Object[] args = new Object[3 + alfabeto.size()];
			args[0] = estado.isInicial() ? ">" : "";
			args[1] = estado.isTerminal() ? "*" : "";
			args[2] = estado.getNome();
			int i = 3;
			for (String simbolo : alfabeto) {
				List<Estado> transicao = estado.getTransicoes().get(simbolo);
				String sep = "";
				String nome = "";
				if (transicao != null) {
					for (Estado estado2 : transicao) {
						nome += sep + estado2.getNome();
						sep = ", ";
					}
				} else {
					nome = "-";
				}
				args[i] = nome;
				i++;
			}
			System.out.format(leftAlignFormat, args);
		}

		System.out.format(headerFotter);

		String valor = "Q = [";
		String sep = "";
		for (Estado estado : estados) {
			valor += sep + estado.getNome();
			sep = ", ";
		}
		valor += "]\nA = " + alfabeto;
		valor += "\nq0 = " + (estadoInicial == null ? "" : estadoInicial.getNome());
		valor += "\nF = [";
		sep = "";
		for (Estado estado : estados) {
			if (estado.isTerminal()) {
				valor += sep + estado.getNome();
				sep = ",";
			}
		}
		valor += "]\n";
		valor += estados;
		return valor;
	}
}
