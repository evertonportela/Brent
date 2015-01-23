package organizador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import aluno.Aluno;

public class OrganizadorSequencial implements IFileOrganizer {

	/**
	 * Canal de escrita e leitura no arquivo
	 */
	private FileChannel canal;

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
	public OrganizadorSequencial(String arqName) throws FileNotFoundException {
		// Cria ou acessa o arquivo no diretório passado
		File file = new File(arqName);

		// Representa um arquivo usado para armazenar os dados que podem ser
		// acessados de maneira não-seqüencial.
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		this.canal = raf.getChannel();
		// raf.close();
	}

	@Override
	public boolean addReg(Aluno pAluno) {
		try {
			long position = this.getPositionMaior(pAluno.getMatricula());

			// Se não encontrar maior ecreve no fim
			if (position == this.INEXISTENTE) {
				canal.write(pAluno.getBuffer(), canal.size());
			} else {
				// Move todos os maiores para frente
				for (long i = canal.size(); i >= position; i -= Aluno.LENGTH) {
					ByteBuffer buff = alocarAluno(i - Aluno.LENGTH,
							Aluno.LENGTH);
					canal.write(buff, i);
					buff.flip();
				}
				// Escreve na posição correta
				canal.write(pAluno.getBuffer(), position);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Aluno getReg(int pMatricula) {
		try {
			long position = this.getPosition(pMatricula);
			if (position == this.INEXISTENTE) {
				return null;
			} else {
				return new Aluno(this.alocarAluno(position, Aluno.LENGTH));
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Aluno delReg(int pMatricula) {
		try {
			Aluno alunoDeletado = null;
			ByteBuffer buff = null;

			long position = this.getPosition(pMatricula);

			if (position == this.INEXISTENTE) {
				return alunoDeletado;
			} else {
				buff = alocarAluno(position, Aluno.LENGTH);
				alunoDeletado = new Aluno(buff);

				// Move todos os maiores para frente caso não seja o ultimo
				if (position + Aluno.LENGTH != (this.canal.size() - 1)) {
					for (long i = position; i < canal.size() - Aluno.LENGTH; i += Aluno.LENGTH) {
						long can = canal.size();
						
						//Pega o próximo aluno
						buff = alocarAluno(i + Aluno.LENGTH, Aluno.LENGTH);
						
						//Sobrescreve o aluno no registro anterior
						canal.write(buff, i);
						buff.flip();
					}
					// Recebe a posição do truncate
					position = this.canal.size() - Aluno.LENGTH;
				}

				this.canal.truncate(position);
			}

			return alunoDeletado;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Obtem a posição de um Aluno no arquivo
	 * 
	 * @param matricula
	 *            Matricula do aluno pesquisado
	 * @return pos posição do aluno no arquivo. Caso não seja encontrado aluno,
	 *         retorna o valor estatico de INEXISTENTE (-1)
	 * @throws IOException
	 */
	private long getPosition(int pMatricula) throws IOException {
		long size = this.canal.size();

		// Coloca o canal na posição 0
		for (long pos = 0; pos < size; pos += Aluno.LENGTH) {
			ByteBuffer buff = alocarAluno(pos, 4);
			int mat = buff.getInt();
			if (pMatricula == mat) {
				return pos;
			} else if (mat > pMatricula) {
				break;
			}
			buff.flip();
		}

		return this.INEXISTENTE;
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
	private ByteBuffer alocarAluno(long pos, int pBuffTamanho)
			throws IOException {
		ByteBuffer buff = ByteBuffer.allocate(pBuffTamanho);
		this.canal.read(buff, pos);
		buff.flip();
		return buff;
	}

	/**
	 * Dada uma matricula, retorna o 1 Aluno com matricula imediatamente maior do que a matricula passada por parametro
	 * @param pMatricula Matricula do Aluno a ser comparada
	 * @return Matricula do Aluno com matricula imediatamente maior do que a matricula passada por parametro
	 * @throws IOException
	 */
	private long getPositionMaior(int pMatricula) throws IOException {
		long size = this.canal.size();

		// Coloca o canal na posição 0
		for (long pos = 0; pos < size; pos += Aluno.LENGTH) {
			ByteBuffer buff = alocarAluno(pos, 4);

			if (buff.getInt() > pMatricula) {
				return pos;
			}
			buff.flip();
		}

		return this.INEXISTENTE;
	}

}
