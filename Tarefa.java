import java.io.Serializable;

public class Tarefa implements Serializable {
    private static final long serialVersionUID = 1L;
    private String descricao;
    private Prioridade prioridade; // "Alta", "Média", "Baixa"
    private boolean completo;

    Tarefa(String decricao, Prioridade prioridade) {
        this.descricao = decricao;
        this.prioridade = prioridade;
        this.completo = false;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public boolean isCompleto() {
        return completo;
    }

    public void setCompleto(boolean completo) {
        this.completo = completo;
    }

    @Override
    public String toString() {
        String check = completo ? "[✓]" : "[ ]";
        return String.format("%s %s - %s", check, prioridade.name(), descricao);
    }
}