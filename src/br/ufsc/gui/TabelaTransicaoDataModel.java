package br.ufsc.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.ufsc.model.Automato;
import br.ufsc.model.Estado;

public class TabelaTransicaoDataModel extends AbstractTableModel {

	private static final long serialVersionUID = 6871872643365655322L;
	private Automato automato;
	private List<Estado> estados = new ArrayList<Estado>();

	public TabelaTransicaoDataModel(Automato automato) {
		this.automato = automato;
		this.estados = new ArrayList<Estado>(automato.getEstados());
	}

	public TabelaTransicaoDataModel() {
		automato = new Automato();
	}

	@Override
	public int getColumnCount() {
		return automato.getAlfabeto().size() + 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Estado";
		}
		return automato.getAlfabeto().get(columnIndex - 1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Estado estado = estados.get(rowIndex);
		if (columnIndex == 0) {
			return (estado.isInicial() ? "->" : "") + (estado.isTerminal() ? "*" : "") + estado.getNome();
		}
		List<Estado> transicoes = estado.getTransicoes().get(automato.getAlfabeto().get(columnIndex - 1));
		String nome = "";
		String sep = "";
		if (transicoes != null) {
			for (Estado estado2 : transicoes) {
				nome += sep + estado2.getNome();
				sep = ",";
			}
		}
		return nome;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		boolean existeEstado = false;
		if (((String) aValue).length() == 0) {
			existeEstado = true;
		} else {
			for (String transicao : ((String) aValue).split(",")) {
				existeEstado = false;
				for (Estado estado : estados) {
					if (estado.getNome().equals(transicao.trim())) {
						existeEstado = true;
						break;
					}
				}
				if (!existeEstado) {
					break;
				}
			}
		}
		if (existeEstado) {
			if (((String) aValue).length() != 0) {
				String simbolo = automato.getAlfabeto().get(columnIndex - 1);
				estados.get(rowIndex).getTransicoes().put(simbolo, null);
				for (String transicao : ((String) aValue).split(",")) {
					try {
						estados.get(rowIndex).addTransicao(simbolo, automato.encontrarEstado(transicao));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	public void setValueAt(String aValue, int rowIndex) {
		// TODO adicao de novo estado
		fireTableCellUpdated(rowIndex, 0);
		fireTableCellUpdated(rowIndex, 1);
		fireTableCellUpdated(rowIndex, 2);
		fireTableCellUpdated(rowIndex, 3);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return automato.getEstados().isEmpty();
	}

	public int getRowCount() {
		return automato.getEstados().size();
	}

	public Automato getAutomato() {
		return automato;
	}

	public void setAutomato(Automato automato) {
		if (automato == null) {
			this.automato = new Automato();
			this.estados = new ArrayList<Estado>();
		} else {
			this.automato = automato;
			this.estados = new ArrayList<Estado>(automato.getEstados());
		}

	}

}
