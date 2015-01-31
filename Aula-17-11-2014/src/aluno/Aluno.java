package aluno;
import java.nio.ByteBuffer;


public class Aluno {
	
	private int matricula; //4bytes
	private String nome; //50bytes
	private String endereco; //60bytes
	private short idade; //2bytes
	private String sexo; //1byte
	private String email; //40bytes
	public static final int LENGTH = 157;
	public static final int MATRICULA_LENGTH = 4;
	
	//Construtor simples
	public Aluno(int matricula, 
			String nome, 
			String endereco, 
			short idade,
			String sexo, 
			String email) {
		
		this.matricula = matricula;
		this.nome = corrigirTamanho(nome, 50);
		this.endereco = corrigirTamanho(endereco, 60);
		this.idade = idade;
		this.sexo = corrigirTamanho(sexo, 1);
		this.email = corrigirTamanho(email, 40);
		
		//chamada corrigir tamanho para cada atributo
		
	}
	
	//Construtor apartir de arquivo
	public Aluno(ByteBuffer buff){
		
		matricula = buff.getInt();
		
		byte[] b_nome = new byte[50];
		buff.get(b_nome);
		nome = new String(b_nome);
		
		byte[] b_endereco = new byte[60];
		buff.get(b_endereco);
		endereco = new String(b_endereco);
		
		idade = buff.getShort();
		
		byte[] b_sexo = new byte[1];
		buff.get(b_sexo);
		sexo = new String(b_sexo);
		
		byte[] b_email = new byte[40];
		buff.get(b_email);
		email = new String(b_email);
		
		
		
	}
	
	public ByteBuffer getBuffer(){
		ByteBuffer buff = ByteBuffer.allocate(157);
		buff.putInt(matricula);
		buff.put(nome.getBytes());
		buff.put(endereco.getBytes());
		buff.putShort(idade);
		buff.put(sexo.getBytes());
		buff.put(email.getBytes());
		buff.flip();
		return buff;
	}
	
	public String corrigirTamanho(String str, int tamanho){
		int len = str.length();
		if(len < tamanho){
			for(int i = len; i < tamanho; i++){
				str = str + " ";
			}
		}
		else{
			str = str.substring(0, tamanho);
		}
		return str;
	}
	
	//Getters e Setters

	public int getMatricula() {
		return matricula;
	}

	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public short getIdade() {
		return idade;
	}

	public void setIdade(short idade) {
		this.idade = idade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Aluno [matricula=" + matricula + ",\n nome=" + nome.trim()
				+ ",\n endereco=" + endereco.trim() + ",\n idade=" + idade + ",\n sexo="
				+ sexo.trim() + ",\n email=" + email.trim() + "]";
	}
}
