import javax.swing.*;
import java.awt.*;

public abstract class CriaTela extends JFrame {

    public CriaTela(String titulo, int largura, int altura){
        setTitle(titulo);
        setSize(largura,altura);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected abstract void configurarComponentes();
}