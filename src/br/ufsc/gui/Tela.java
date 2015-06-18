package br.ufsc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import test.Testes;
import br.ufsc.model.Automato;
import br.ufsc.model.GramaticaRegular;
import br.ufsc.model.simone.ExpressaoRegular;

public class Tela extends JFrame {

	private static final long serialVersionUID = -1807554966827177138L;
	private JPanel contentPane;
	private JTable table;
	private TabelaTransicaoDataModel dataModel;
	private JTextField textReconhecerSentenca;
	private JTextField textRegex;
	private JTextArea textGramatica;
	private Tela tela = this;
	private JFileChooser fc = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (UnsupportedLookAndFeelException e) {
					} catch (ClassNotFoundException e) {
					} catch (InstantiationException e) {
					} catch (IllegalAccessException e) {
					}
					Tela frame = new Tela();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Tela() {
		super("Automato");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 450);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(600, 400));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);

		JMenuItem mntmNovo = new JMenuItem("Novo");
		mnArquivo.add(mntmNovo);

		JMenuItem mntmCarregarAutomato = new JMenuItem("Carregar Automato");
		mntmCarregarAutomato.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = fc.showOpenDialog(tela);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						FileInputStream fileIn = new FileInputStream(file);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						dataModel.setAutomato((Automato) in.readObject());
						dataModel.fireTableStructureChanged();
						in.close();
						fileIn.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		mnArquivo.add(mntmCarregarAutomato);

		JMenuItem mntmSalvarAutomato = new JMenuItem("Salvar Automato");
		mntmSalvarAutomato.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = fc.showSaveDialog(tela);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						FileOutputStream fileOut;
						fileOut = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fileOut);
						out.writeObject(dataModel.getAutomato());
						out.close();
						fileOut.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Não foi possível salvar o automato.");
				}
			}
		});
		mnArquivo.add(mntmSalvarAutomato);

		JMenuItem mntmCarregarGramatica = new JMenuItem("Carregar Gramática");
		mntmCarregarGramatica.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String texto = carregarTexto();
					setGramatica(texto);
					textGramatica.setText(texto);
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Arquivo inválido.");
				}
			}
		});
		mnArquivo.add(mntmCarregarGramatica);

		JMenuItem mntmSalvarGramatica = new JMenuItem("Salvar Gramática");
		mntmSalvarGramatica.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String regex = textGramatica.getText();
				if (regex.length() != 0) {
					salvarTexto(regex);
				} else {
					JOptionPane.showMessageDialog(null, "Insira uma gramática antes de salvar.");
				}
			}
		});
		mnArquivo.add(mntmSalvarGramatica);

		JMenuItem mntmSalvarER = new JMenuItem("Salvar ER");
		mntmSalvarER.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String regex = textRegex.getText();
				if (regex.length() != 0) {
					salvarTexto(regex);
				} else {
					JOptionPane.showMessageDialog(null, "Insira uma expressão regular antes de salvar.");
				}
			}
		});
		mnArquivo.add(mntmSalvarER);

		JMenuItem mntmCarregarER = new JMenuItem("Carregar ER");
		mntmCarregarER.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String texto = carregarTexto();
					criaRegex(texto);
					textRegex.setText(texto);
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Arquivo inválido.");
				}
			}
		});
		mnArquivo.add(mntmCarregarER);

		JMenuItem mntmBuscarPadrao = new JMenuItem("Buscar padrão");
		mntmBuscarPadrao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (textRegex.getText().length() != 0) {
					String texto = JOptionPane.showInputDialog("Digite o texto para efetuar a busca de padrões.");
					Pattern pattern = Pattern.compile(textRegex.getText());
					Matcher matcher = pattern.matcher(texto);
					String padroes = "";
					while (matcher.find()) {
						String group = matcher.group();
						if (group.length() != 0) {
							padroes += matcher.group() + "\n";
						}
					}
					JOptionPane.showMessageDialog(null, "Padrões encontrados:\n" + padroes);
				} else {
					JOptionPane.showMessageDialog(null, "Insira uma expressão regular no campo correspondente.");
				}
			}
		});
		mnArquivo.add(mntmBuscarPadrao);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_centerPanel.rowHeights = new int[] { 0, 0 };
		gbl_centerPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_centerPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		centerPanel.setLayout(gbl_centerPanel);

		JPanel leftPanel = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		centerPanel.add(leftPanel, gbc_panel_1);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_leftPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		leftPanel.setLayout(gbl_leftPanel);

		JButton btnNovoAutomato = new JButton("Novo Automato");
		btnNovoAutomato.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				novoAutomato();
			}
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 0;
		gbc_btnNewButton_2.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnNovoAutomato, gbc_btnNewButton_2);

		JButton btnComparacao = new JButton("Comparar GR e ER");
		btnComparacao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(Testes.aEstrela().equals(Testes.aEstrela()));
				// TODO
				Automato gr = null;
				Automato er = null;
				try {
					JOptionPane.showMessageDialog(null, "Selecione a gramática");
					String texto = carregarTexto();
					GramaticaRegular gramatica = new GramaticaRegular();
					String[] linhas = texto.split("\n");
					gramatica.setEstadoInicial(linhas[0].split("\\s*->\\s*")[0]);
					for (String linha : linhas) {
						String[] linhaSplit = linha.split("\\s*->\\s*");
						String estado = linhaSplit[0];
						for (String derivacao : linhaSplit[1].split("\\|")) {
							boolean ok = gramatica.adicionarDerivacao(estado, derivacao.trim().replace("\r", ""));
							if (!ok) {
								throw new IllegalArgumentException("Gramática incorreta:\n" + texto);
							}
						}
					}
					gr = gramatica.gerarAutomato();

					JOptionPane.showMessageDialog(null, "Selecione a expressão regular");
					texto = carregarTexto();
					er = ExpressaoRegular.obterAutomatoDaExpressaoRegular(texto);
					if (gr.equals(er)) {
						JOptionPane.showMessageDialog(null, "São equivalentes");
					} else {
						JOptionPane.showMessageDialog(null, "Não são equivalentes");
					}
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Arquivo inválido.");
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_21 = new GridBagConstraints();
		gbc_btnNewButton_21.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_21.gridx = 1;
		gbc_btnNewButton_21.gridy = 0;
		gbc_btnNewButton_21.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnComparacao, gbc_btnNewButton_21);

		JButton btnDeterminizar = new JButton("Determinizar");
		btnDeterminizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataModel.setAutomato(dataModel.getAutomato().determinizar());
				dataModel.fireTableStructureChanged();
			}
		});
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_4.gridx = 0;
		gbc_btnNewButton_4.gridy = 1;
		gbc_btnNewButton_4.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnDeterminizar, gbc_btnNewButton_4);

		JButton btnMinimizar = new JButton("Minimizar");
		btnMinimizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataModel.setAutomato(dataModel.getAutomato().obterAutomatoMinimo());
				dataModel.fireTableStructureChanged();
			}
		});
		GridBagConstraints gbc_btnNewButton_6 = new GridBagConstraints();
		gbc_btnNewButton_6.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_6.gridx = 1;
		gbc_btnNewButton_6.gridy = 1;
		gbc_btnNewButton_6.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnMinimizar, gbc_btnNewButton_6);

		JButton btnInterseccao = new JButton("Intersecção");
		btnInterseccao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Selecione o arquivo que contém o segundo automato usado na intersecção.");
				try {
					int returnVal = fc.showOpenDialog(tela);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						FileInputStream fileIn = new FileInputStream(file);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						dataModel.setAutomato(dataModel.getAutomato().interseccao((Automato) in.readObject()));
						dataModel.fireTableStructureChanged();
						recolorirSentenca();
						in.close();
						fileIn.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_8 = new GridBagConstraints();
		gbc_btnNewButton_8.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_8.gridx = 0;
		gbc_btnNewButton_8.gridy = 3;
		gbc_btnNewButton_8.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnInterseccao, gbc_btnNewButton_8);

		JButton btnComplemento = new JButton("Complemento");
		btnComplemento.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataModel.setAutomato(dataModel.getAutomato().obterComplemento());
				dataModel.fireTableStructureChanged();
				recolorirSentenca();
			}
		});
		GridBagConstraints gbc_btnNewButton_9 = new GridBagConstraints();
		gbc_btnNewButton_9.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_9.gridx = 1;
		gbc_btnNewButton_9.gridy = 3;
		gbc_btnNewButton_9.fill = GridBagConstraints.BOTH;
		leftPanel.add(btnComplemento, gbc_btnNewButton_9);

		JPanel panelReconhecerSentenca = new JPanel();
		TitledBorder title3 = BorderFactory.createTitledBorder("Reconhecer sentença");
		panelReconhecerSentenca.setBorder(title3);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 5;
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		leftPanel.add(panelReconhecerSentenca, gbc_btnNewButton);
		panelReconhecerSentenca.setLayout(new BorderLayout(0, 0));

		textReconhecerSentenca = new JTextField();

		textReconhecerSentenca.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				recolorirSentenca();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				recolorirSentenca();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelReconhecerSentenca.add(textReconhecerSentenca);
		textReconhecerSentenca.setColumns(10);

		JPanel rightPanel = new JPanel();
		TitledBorder title2 = BorderFactory.createTitledBorder("Tabela de transições");
		rightPanel.setBorder(title2);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;

		JPanel panelRegex = new JPanel();
		TitledBorder title4 = BorderFactory.createTitledBorder("Regex para Autômato");
		panelRegex.setBorder(title4);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 6;
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.fill = GridBagConstraints.BOTH;
		leftPanel.add(panelRegex, gbc_btnNewButton_1);
		panelRegex.setLayout(new BorderLayout(0, 0));

		textRegex = new JTextField();
		panelRegex.add(textRegex, BorderLayout.CENTER);
		textRegex.setColumns(10);

		JButton btnRegex = new JButton("Ok");
		btnRegex.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					criaRegex(textRegex.getText());
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		panelRegex.add(btnRegex, BorderLayout.EAST);

		JPanel panelGramatica = new JPanel();
		TitledBorder title5 = BorderFactory.createTitledBorder("Gramática para automato");
		panelGramatica.setBorder(title5);
		GridBagConstraints gbc_btnNewButton_11 = new GridBagConstraints();
		gbc_btnNewButton_11.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_11.gridx = 0;
		gbc_btnNewButton_11.gridy = 7;
		gbc_btnNewButton_11.gridwidth = 2;
		gbc_btnNewButton_11.fill = GridBagConstraints.BOTH;
		leftPanel.add(panelGramatica, gbc_btnNewButton_11);
		panelGramatica.setLayout(new BorderLayout(0, 0));

		textGramatica = new JTextArea();
		JScrollPane scrollPaneGramatica = new JScrollPane(textGramatica);
		panelGramatica.add(scrollPaneGramatica, BorderLayout.CENTER);

		JPanel panelBtnGramatica2 = new JPanel(new GridBagLayout());
		JButton btnGramatica2 = new JButton("Ok");
		btnGramatica2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					setGramatica(textGramatica.getText());
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Gramática inválida.");
				}
			}
		});
		panelBtnGramatica2.add(btnGramatica2);
		panelGramatica.add(panelBtnGramatica2, BorderLayout.EAST);

		centerPanel.add(rightPanel, gbc_panel);
		rightPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataModel = new TabelaTransicaoDataModel(Testes.criaImparDivisivelPor3());
		table.setModel(dataModel);
		scrollPane.setViewportView(table);
	}

	public void criaRegex(String texto) throws IllegalArgumentException {
		if (texto.length() != 0) {
			dataModel.setAutomato(ExpressaoRegular.obterAutomatoDaExpressaoRegular(texto));
			dataModel.fireTableStructureChanged();
			recolorirSentenca();
		}
	}

	public void novoAutomato() {
		Automato novo = new Automato();
		String alfabeto = JOptionPane.showInputDialog("Informe os símbolos do autômato\nEx: abcd");
		for (int i = 0; i < alfabeto.length(); i++) {
			novo.addSimbolo(alfabeto.charAt(i) + "");
		}
		String estados = JOptionPane.showInputDialog("Informe os estados separados por vírgula e somente letras maiúsculas.\nEx: S,A,B");
		for (String estado : estados.split(",")) {
			novo.addEstado(estado.trim());
		}
		String estadosFinais = JOptionPane.showInputDialog("Informe os estados finais separados por vírgula e somente letras maiúsculas.\nEx: S,A");
		for (String estado : estadosFinais.split(",")) {
			novo.addEstadoFinal(estado);
		}
		String estadoInicial = JOptionPane.showInputDialog("Informe o estado inicial");
		try {
			novo.setEstadoInicial(estadoInicial);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataModel.setAutomato(novo);
		dataModel.fireTableStructureChanged();
	}

	public void setGramatica(String gramaticaSTR) throws IllegalArgumentException, Exception {
		System.out.println(gramaticaSTR);
		GramaticaRegular gramatica = new GramaticaRegular();
		String[] linhas = gramaticaSTR.split("\n");
		gramatica.setEstadoInicial(linhas[0].split("\\s*->\\s*")[0]);
		for (String linha : linhas) {
			String[] linhaSplit = linha.split("\\s*->\\s*");
			String estado = linhaSplit[0];
			for (String derivacao : linhaSplit[1].split("\\|")) {
				boolean ok = gramatica.adicionarDerivacao(estado, derivacao.trim().replace("\r", ""));
				if (!ok) {
					throw new IllegalArgumentException("Gramática incorreta:\n" + gramaticaSTR);
				}
			}
		}
		dataModel.setAutomato(gramatica.gerarAutomato());
		dataModel.fireTableStructureChanged();
	}

	public void salvarTexto(String texto) {
		byte dataToWrite[] = texto.getBytes();
		FileOutputStream out;
		try {
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				out = new FileOutputStream(file);
				out.write(dataToWrite);
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String carregarTexto() throws IOException {
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			byte[] encoded = Files.readAllBytes(file.toPath());
			return new String(encoded, "UTF-8");
		}
		return "";
	}

	public void recolorirSentenca() {
		String sentenca = textReconhecerSentenca.getText();
		if (dataModel.getAutomato().reconhecerSentenca(sentenca)) {
			textReconhecerSentenca.setForeground(Color.blue);
		} else {
			textReconhecerSentenca.setForeground(Color.red);
		}
	}
}
