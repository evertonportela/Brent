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
	//private final long VALOR_PRIMO = QTD_REGISTROS_APRESENTACAO;
	private final long VALOR_PRIMO = QTD_REGISTROS_MIGRACAO;

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
		long finalSaltos = 0;
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

				// Melhor não mover o registro atual
				if (saltosInserirNovoAluno <= saltosMoverAlunoExistente) {
					finalSaltos = calcularPosicao(pAluno, inicioLista,
							saltosInserirNovoAluno);
										
					canal.write(pAluno.getBuffer(), finalSaltos);
				}
				else {
					// Inseri o novo registro no lugar do atual
					//e move o outro para uma nova posição
					Aluno alunoMover = new Aluno(alocarAluno(inicioLista,
							Aluno.LENGTH));
					
					// inseri na posição do aluno a ser movido
					canal.write(pAluno.getBuffer(), inicioLista);
					
					finalSaltos = calcularPosicao(alunoMover, inicioLista,
							saltosMoverAlunoExistente);

					// escreve alunoMover na nova posição
					canal.write(alunoMover.getBuffer(), finalSaltos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * - Calcula a posição que o registro será inserido de 
	 * acordo com o incremento, a quantidade de saltos e a posição inial.
	 * @param pAluno
	 * @param inicioLista
	 * @param saltosInserirNovoAluno
	 * @return
	 * @throws IOException
	 */
	private long calcularPosicao(Aluno pAluno, long inicioLista,
			int saltosInserirNovoAluno) throws IOException {
		long finalSaltos;
		finalSaltos = inicioLista
				+ (saltosInserirNovoAluno * getIncremento(pAluno
						.getMatricula()));
		
		// Verifica se a posição é maior que o canal
		finalSaltos = isOverFlow(finalSaltos);
		
		return finalSaltos;
	}
	
	/**
	 * - Se a posição for maior que o canal retorna:
	 * 	retorna o resto da divisão da posição pelo tamanho do canal
	 * @param pPosition
	 * @return
	 * @throws IOException
	 */
	private long isOverFlow(long pPosition) throws IOException{
		if(pPosition >= canal.size()){
			return pPosition % canal.size();
		}
		
		return pPosition;
	}

	@Override
	public Aluno getReg(int pMatricula) {
		Aluno aluno = null;
		try {
			// Recupera a posição
			long position = this.getPosition(pMatricula);
			if(position == INEXISTENTE){
				return aluno;
			}
			
			// Aloca o aluno
			ByteBuffer buff = alocarAluno(position, Aluno.LENGTH);
			aluno = new Aluno(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return aluno;
	}

	@Override
	public Aluno delReg(int pMatricula) {
		Aluno aluno = null;
		try {
			// recupera posição
			long position = getPosition(pMatricula);
			if(position==INEXISTENTE){
				return aluno;
			}
			
			// aloca registro
			aluno = new Aluno(alocarAluno(position, Aluno.LENGTH));
			
			// escreve um aluno vazio na posição recebida
			Aluno alunoVazio = new Aluno((int) INEXISTENTE, "", "", (short) 0, "", "");
			this.canal.write(alunoVazio.getBuffer(), position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return aluno;
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
		// usado na migração
		return ((pMatricula % (this.VALOR_PRIMO - 2)) + 1) * Aluno.LENGTH;
		
		// usado na apresentação
		//return ((pMatricula/QTD_REGISTROS_APRESENTACAO) % QTD_REGISTROS_APRESENTACAO) * Aluno.LENGTH;
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
	 * @throws IOException 
	 */
	private int saltosAteNovaPosicao(long pPosition, int qtSaltos, long pIncremento) throws IOException {
		// atulisado para quando o salto voltar ao inicio da lista
		long position = pPosition;
		if(pPosition > (canal.size()-1)){
			position = pPosition - canal.size();
		}

		if(isPosicaoVazia(position)){
			return qtSaltos;
		}else{
			return saltosAteNovaPosicao(position + pIncremento, qtSaltos + 1, pIncremento);
		}
	}
	
	/**
	 * - Chama a recursão
	 * @param pMatricula
	 * @return
	 * @throws IOException
	 */
	private long getPosition(int pMatricula) throws IOException {
		return getPosition(pMatricula, getHash(pMatricula),
				getIncremento(pMatricula));
	}
	
	/**
	 * - Encontra a posição da matricula passada
	 * @param pMatricula
	 * @param pPosition
	 * @param pIncremento
	 * @return
	 * @throws IOException
	 */
	private long getPosition(int pMatricula, long pPosition, long pIncremento) throws IOException {
		// atulisado para quando o salto voltar ao inicio da lista
		long positionAtual = pPosition;
		if(pPosition >= canal.size()){
			positionAtual = pPosition - canal.size();
		}
		
		int matriculaAluno = alocarAluno(positionAtual, Aluno.MATRICULA_LENGTH).getInt();
		if (matriculaAluno == 0) {
			return INEXISTENTE;
		}else if(matriculaAluno == pMatricula){
			return positionAtual;
		}else{
			return getPosition(pMatricula, positionAtual + pIncremento, pIncremento);
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
