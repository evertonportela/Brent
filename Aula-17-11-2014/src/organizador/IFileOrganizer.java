package organizador;

import aluno.Aluno;

/**
 * Interface genérica que define as operações de organizadores de arquivos de
 * alunos em disco.
 * 
 * @author Tarcisio Rocha
 * 
 */
public interface IFileOrganizer {

	/**
	 * Dada uma instância da classe Aluno, este método adiciona os dados da
	 * instância em um arquivo seguindo o método de organização de arquivos
	 * especificado.
	 * 
	 * @param aluno
	 *            Instância da classe Aluno
	 * @return True se o registro foi adicionado com sucesso; False se o
	 *         registro não pode ser adicionado.
	 */
	public boolean addReg(Aluno aluno);

	/**
	 * Dado um número de matrícula, este método consulta o arquivo de alunos e
	 * devolve uma instância que encapsula aos dados do aluno que contém a
	 * matrícula fornecida.
	 * 
	 * @param matricula
	 *            Número de matrícula para a consulta.
	 * @return Instância da classe Aluno correspondente à matrícula fornecida;
	 *         Null se a matrícula informada não existe no arquivo.
	 */
	public Aluno getReg(int matricula);

	/**
	 * Dado um número de matrícula, localiza e exclui o registro do arquivo de
	 * alunos que corresponde à matrícula fornecida.
	 * 
	 * @param matricula
	 *            Matrícula do aluno a ser excluído.
	 * @return true ou false indicando se o aluno foi ou não removido,
	 *         respectivamente.
	 */
	public Aluno delReg(int matricula);

}
