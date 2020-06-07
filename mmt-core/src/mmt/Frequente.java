package mmt;

import java.io.Serializable;

/** Um passageiro Frequente, uma vez que tem 10% de desconto, paga 0.9
do preço original. Da mesma forma ocorre para as restantes categorias. */
public class Frequente extends Category implements Serializable {
	public double discount() {
		return 0.9;
	}
}