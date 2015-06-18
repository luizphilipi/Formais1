/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model.simone;

/**
 *
 * @author decker
 */
public class No {

	// Atributos========================
	// No pai
	No pai;
	// Filho da esquerda
	No filhoEsquerda;
	// Filho da direita
	No filhoDireita;
	// No que recebeu a costura desse no
	No costurado;
	// Simbolo do no
	char simbolo;
	// Numero de controle
	int controle;

	// Contrutores======================
	// Construtor vazio. Usado para casos de no temporário.
	No() {
	}

	// No folha
	No(char simbolo, No pai) {
		this.simbolo = simbolo;
		this.pai = pai;
	}

	// No não-folha (operador)
	No(char simbolo, No pai, No filhoEsquerda, No filhoDireita) {
		this.simbolo = simbolo;
		this.pai = pai;
		this.filhoDireita = filhoDireita;
		this.filhoEsquerda = filhoEsquerda;
	}
}
