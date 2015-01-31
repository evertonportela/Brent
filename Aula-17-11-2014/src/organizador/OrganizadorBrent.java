package organizador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import aluno.Aluno;

public class OrganizadorBrent implements IFileOrganizer {

	/**
	 * Quantidade de registros utilizados na migração
	 */
	private final long QTD_REGISTROS_MIGRACAO = 8000009;  

	/**
	 * Quantidade de registros utilizados na apresentação do trabalho
	 */
	private final long QTD_REGISTROS_APRESENTACAO = 11;  
	
	/**
	 * Canal de escrita e leitura no arquivo
	 */
	private FileChannel canal;

	/**
	 * valor primo que corresponde ao tamanho da tabela.
	 */
	private final long VALOR_PRIMO = QTD_REGISTROS_APRESENTACAO;

	/**
	 * Valor estatico usado para indicar que a consulta não retornou registro
	 */
	private static final long INEXISTENTE = -1;

	/**
	 * 
	 * @param arqName
	 *            Nome do arquivo a ser utilizado como fonte de dados
	 * @throws FileNotFoundException
	 */
	public OrganizadorBrent(String arqName) throws FileNotFoundException {
		// Cria ou acessa o arquivo no diretório passado
		File file = new File(arqName);

		// Representa um arquivo usado para armazenar os dados
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		this.canal = raf.getChannel();
	}

	@Override
	public boolean addReg(Aluno pAluno) {
		long inicioLista = 0;
		int saltosInserirNovoAluno = 0;
		int saltosMoverAlunoExistente = 0;
		long finalLista = 0;
		// caso base quando Espaço vazio ou apagado
		try {
			inicioLista = getHash(pAluno.getMatricula());
			if (isPosicaoVazia(inicioLista)) {
				// escreve o aluno na posição vazia
				canal.write(pAluno.getBuffer(), inicioLista);
			} else {
				saltosInserirNovoAluno = saltosAteNovaPosicao(inicioLista,
						0, this.getIncremento(pAluno.getMatricula()));
				
				// Recupera matricula do aluno na posição e calcula quantidade de saltos
				int matriculaAlunoPosAtual = alocarAluno(inicioLista, Aluno.MATRICULA_LENGTH).getInt();
				saltosMoverAlunoExistente = saltosAteNovaPosicao(inicioLista, 0,
						this.getIncremento(matriculaAlunoPosAtual));

				if (saltosInserirNovoAluno < saltosMoverAlunoExistente) {
					// verificar se o aluno que está no posição pertence a ela ou se foi movido
					if(inicioLista == this.getHash(matriculaAlunoPosAtual)){

						// esta é a posição inicial então inseri o novo no fim dos saltos
						finalLista = inicioLista
								+ ((saltosInserirNovoAluno + 1) * getIncremento(pAluno
										.getMatricula()));
						canal.write(pAluno.getBuffer(), finalLista);
					}else{
						// deve calcular a quantidade de saltos desde a posição real do aluno
						
					}
				}
				else {
					// recuperar aluno a ser movido para nova posição
					Aluno alunoMover = new Aluno(alocarAluno(inicioLista,
							Aluno.LENGTH));
					
					// inseri na posição do aluno a ser movido
					canal.write(pAluno.getBuffer(), inicioLista);
					
					// calcula próxima posição vazia
					finalLista = inicioLista
							+ ((saltosMoverAlunoExistente+1) // Soma para obter posição real 
									* getIncremento(alunoMover
									.getMatricula()));
					
					// escreve alunoMover na nova posição
					canal.write(alunoMover.getBuffer(), finalLista);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Aluno getReg(int pMatricula) {
		return null;
	}

	@Override
	public Aluno delReg(int pMatricula) {
		return null;
	}

	/**
	 * - Calcula o Hash da Matricula. Hash[chave] = chave mod P onde P é um
	 * valor primo que corresponde ao tamanho da tabela.
	 * @param ppMatricula
	 * @return
	 * @throws IOException
	 */
	private long getHash(long pMatricula) {
		//- Multiplca pela quantidade de bytes que o aluno possui para
		//obter a real posição do aluno no array
		return (pMatricula % this.VALOR_PRIMO) * Aluno.LENGTH;
	}

	/**
	 * - Calcula o Incremento da Matricula. Inc(chave) = (chave mod (P-2)) + 1
	 * onde P é um valor primo que corresponde ao tamanho da tabela.
	 * 
	 * @param ppMatricula
	 * @return
	 */
	private long getIncremento(long pMatricula) {
		return ((pMatricula % (this.VALOR_PRIMO - 2)) + 1) * Aluno.LENGTH;
	}


	/**
	 * Aloca uma instância de Aluno ou parte dos atributos deste em um
	 * ByteBuffer para recuperar os valores gravados
	 * 
	 * @param pos
	 *            Posição apartir da qual ira obter os valores do aluno
	 * @param pBuffTamanho
	 *            Tamanho do buffer. Varia a depender do que pretende obter.
	 *            Para matricula, apenas 4, matricula e nome 4 + 50...
	 * @return buff ByteBuffer
	 * @throws IOException
	 */
	private ByteBuffer alocarAluno(long pos, int pBuffTamanho) {
		ByteBuffer buff = ByteBuffer.allocate(pBuffTamanho);
		try {
			this.canal.read(buff, pos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buff.flip();
		return buff;
	}

	/**
	 * Calcula a quantidade de saltos até encontrar uma posição livre para
	 * inserir
	 * 
	 * @param pMatricula
	 * @param pPosition
	 * @param qtSaltos
	 * @param pIncremento 
	 * @return quantidade de saltos até encontrar posição livre
	 */
	private int saltosAteNovaPosicao(long pPosition, int qtSaltos, long pIncremento) {
		if(isPosicaoVazia(pPosition)){
			return qtSaltos;
		}else{
			return saltosAteNovaPosicao(pPosition + pIncremento, qtSaltos + 1, pIncremento);
		}
	}

	/**
	 * Verifica se na posição passada existe um aluno válido ou se é vazia
	 * 
	 * @param pPosition
	 *            posição do arquivo
	 * @return True se for vazio False se existir aluno válido
	 */
	private boolean isPosicaoVazia(long pPosition) {
		// Recupera a matricula do aluno existente na posição passada
		int matriculaAluno = alocarAluno(pPosition, Aluno.MATRICULA_LENGTH).getInt();
		if (matriculaAluno == 0 || matriculaAluno == INEXISTENTE) {
			return true;
		}

		return false;
	}
}
