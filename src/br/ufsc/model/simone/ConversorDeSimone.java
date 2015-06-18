/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model.simone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Converte uma Regex em FDA utilizando o metodo de DeSimone
 *
 * @author decker
 */
public class ConversorDeSimone {

    //Construtor padrão
    public ConversorDeSimone() {

    }

    public br.ufsc.model.Automato criarAutomato(Arvore arvore) throws Exception {
        List<EstadoComposto> estados = new ArrayList<>();
        Stack<EstadoComposto> aFazer = new Stack<>();
        Integer estado = 0;
        EstadoComposto inicial = new EstadoComposto("q" + estado);
        List<EstadoCompostoVerificado> percorridosRaiz = new ArrayList<>();
        analisarArvore(arvore.raiz, Boolean.FALSE, inicial.composicao, percorridosRaiz);
        aFazer.push(inicial);
        estados.add(inicial);

        List<Character> alfabeto = obterAlfabeto(arvore.folhas);

        while (!aFazer.isEmpty()) {
            EstadoComposto alce = aFazer.pop();
            //Contar o numero de entradas diferentes aceitas
            List<Character> operadores = new ArrayList<>();

            for (No no : alce.composicao) {
                Character atual = no.simbolo;
                if (!operadores.contains(atual) && atual.compareTo('&') != 0) {
                    operadores.add(atual);
                }
            }

            for (Character operador : operadores) {
                estado++;
                EstadoComposto novoEstado = new EstadoComposto("q" + estado);
                List<No> composicoesSimbolo = obterComposicoesSimbolo(alce.composicao, operador);
                for (No composicao : composicoesSimbolo) {
                    List<EstadoCompostoVerificado> percorridos = new ArrayList<>();
                    analisarArvore(composicao.costurado, Boolean.TRUE, novoEstado.composicao, percorridos);
                }
                Boolean existente = Boolean.FALSE;
                for (int i = 0; i < estados.size() && !existente; i++) {
                    Integer posicaoEquivalente = verificarEquivalencia(novoEstado.composicao, estados);
                    if (posicaoEquivalente != -1) {//caso bug
                        existente = Boolean.TRUE;
                        alce.transicoes.add(new Transicao(operador, estados.get(posicaoEquivalente)));
                    } else {
                        existente = Boolean.TRUE;
                        estados.add(novoEstado);
                        alce.transicoes.add(new Transicao(operador, novoEstado));
                        aFazer.push(novoEstado);

                    }
                }
            }
        }
        return estados2Automato(estados, alfabeto);
    }

    private br.ufsc.model.Automato estados2Automato(List<EstadoComposto> estados, List<Character> alfabeto) throws Exception {
        br.ufsc.model.Automato automato = new br.ufsc.model.Automato();
        for (Character simbolo : alfabeto) {
        	automato.addSimbolo(simbolo.toString());
		}
        br.ufsc.model.Estado inicial = new br.ufsc.model.Estado(estados.get(0).estado, ehFinal(estados.get(0).composicao));
        automato.addEstado(inicial.getNome());
        automato.setEstadoInicial(inicial.getNome());
        if (ehFinal(estados.get(0).composicao)) {
            automato.addEstadoFinal(inicial.getNome());
        }

        //Segura peao
        for (int i = 1;//Pq nao queremos add o 1 de novo
                i < estados.size();
                i++) {
            automato.addEstado(estados.get(i).estado);
            if (ehFinal(estados.get(i).composicao)) {
                automato.addEstadoFinal(estados.get(i).estado);
            }

        }//Já adicionamos todos os estados pro automato
        for (int i = 0; i < automato.obterNumeroEstados(); i++) {
            for (int j = 0; j < estados.get(i).transicoes.size(); j++) {
                String estadoAtual = estados.get(i).estado;
                String simbolo = estados.get(i).transicoes.get(j).input.toString();
                String proximoEstado = estados.get(i).transicoes.get(j).proximoEstado.estado;
                automato.addTransicao(estadoAtual, simbolo, proximoEstado);
            }
        }
        return automato;

    }

    private boolean ehFinal(List<No> composicao) {
        for (No no : composicao) {
            if (no.simbolo == '&') {
                return true;
            }
        }
        return false;
    }

    private Integer verificarEquivalencia(List<No> composicao, List<EstadoComposto> estados) {
        Boolean equivalente = Boolean.FALSE;
        for (int i = 0; i < estados.size() && !equivalente; i++) {
            equivalente = compararComposicoes(composicao, estados.get(i).composicao);
            if (equivalente) {
                return i;
            }
        }
        return -1; //bugou
    }

    private Boolean compararComposicoes(List<No> composicoesNovo, List<No> composicao) {
        if (composicoesNovo.size() != composicao.size()) {
            return false; //O tamanho das duas composições sao diferentes, logo, nao sao equivalentes!
        }
        for (int i = 0; i < composicoesNovo.size(); i++) {
            No atual = composicoesNovo.get(i);
            Boolean correspondente = Boolean.FALSE;
            for (int j = 0; j < composicao.size() && !correspondente; j++) {
                if (atual.controle == composicao.get(j).controle) {
                    correspondente = Boolean.TRUE;
                }
            }
            if (!correspondente) {
                return false;
            }
        }
        return true;
    }

    private List<No> obterComposicoesSimbolo(List<No> composicao, Character simbolo) {
        List<No> composicoesSimbolo = new ArrayList<>();
        Iterator<No> iterador = composicao.listIterator();
        while (iterador.hasNext()) {
            No next = iterador.next();
            if (next.simbolo == simbolo) {
                composicoesSimbolo.add(next);
            }
        }
        return composicoesSimbolo;
    }

    //composicao de um estado e suas transicoes (recursivo)
    class EstadoCompostoVerificado {

        No no;
        Boolean direcao;
//Estrutura para os nos ja verificados, e em que direcao foram verificados

        public EstadoCompostoVerificado(No no, Boolean direcao) {
            this.no = no;
            this.direcao = direcao;
        }

    }

    private void analisarArvore(No no, Boolean direcao, List<No> composicao, List<EstadoCompostoVerificado> percorridos) {
        if (composicao.contains(no)) {
            return;
        }
        Character simboloNo = no.simbolo;
        if (direcao) {
            switch (simboloNo) {
                //ponto pra cima
                case '.':
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoDireita, Boolean.FALSE, composicao, percorridos);
                    }
                    break;

                case '|':
                    //Corre pra baixo
                    while (no.filhoDireita != null) {
                        no = no.filhoDireita;
                    }
                    if (jaPercorrido(percorridos, Boolean.TRUE, no)) {
                        analisarArvore(no.costurado, Boolean.TRUE, composicao, percorridos);
                    }
                    break;
                case '*':
                    if (jaPercorrido(percorridos, Boolean.TRUE, no)) {
                        analisarArvore(no.costurado, Boolean.TRUE, composicao, percorridos);
                    }
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoEsquerda, Boolean.FALSE, composicao, percorridos);
                    }
                    break;
                case '?':
                    if (jaPercorrido(percorridos, Boolean.TRUE, no)) {
                        analisarArvore(no.costurado, Boolean.TRUE, composicao, percorridos);
                    }
                    break;
                default://Caso FOLHA!
                    composicao.add(no);
                    break;
            }

        }//trocando de direcao!
        else {
            switch (simboloNo) {
                case '.':
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoEsquerda, Boolean.FALSE, composicao, percorridos);
                    }
                    break;
                case '|':
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoDireita, Boolean.FALSE, composicao, percorridos);
                    }
                    break;
                case '*':
                    if (jaPercorrido(percorridos, Boolean.TRUE, no)) {
                        analisarArvore(no.costurado, Boolean.TRUE, composicao, percorridos);
                    }
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoEsquerda, Boolean.FALSE, composicao, percorridos);
                    }

                    break;
                case '?':
                    if (jaPercorrido(percorridos, Boolean.TRUE, no)) {
                        analisarArvore(no.costurado, Boolean.TRUE, composicao, percorridos);
                    }
                    if (jaPercorrido(percorridos, Boolean.FALSE, no)) {
                        analisarArvore(no.filhoEsquerda, Boolean.FALSE, composicao, percorridos);
                    }
                    break;
                default://Caso FOLHA!
                    composicao.add(no);
                    break;
            }
        }
    }

    private List<Character> obterAlfabeto(List<No> folhas) {
        List<Character> alfabeto = new ArrayList<>();
        for (No no : folhas) {
            Character alce = no.simbolo;
            if (!alfabeto.contains(alce) && alce.compareTo('&') != 0) {
                alfabeto.add(alce);
            }
        }
        return alfabeto;
    }

    // Verifica se um no ja foi verificado nessa direção
    public boolean jaPercorrido(List<EstadoCompostoVerificado> percorrido, Boolean direcao, No noAtual) {
        for (EstadoCompostoVerificado estado : percorrido) {
            if (estado.direcao == direcao && estado.no.equals(noAtual)) {
                return false; // Ja percorrido
            }
        }
        percorrido.add(new EstadoCompostoVerificado(noAtual, direcao));
        return true;

    }

    class EstadoComposto {

        String estado;
        List<Transicao> transicoes = new ArrayList<>();
        List<No> composicao = new ArrayList<>();

        public EstadoComposto(String estado) {
            this.estado = estado;
        }

    }

//abstracao da transicao de um estado para outro
    class Transicao {

        Character input;
        EstadoComposto proximoEstado;

        public Transicao(Character input, EstadoComposto proximoEstado) {
            this.input = input;
            this.proximoEstado = proximoEstado;
        }

    }

}
