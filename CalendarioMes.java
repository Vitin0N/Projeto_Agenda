import java.time.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalendarioMes extends CriaTela{

    private YearMonth anoMesAtual;
    private JPanel panelDias;
    private JLabel titulo;
    private static final String[] mesesIdx = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                                              "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

    public CalendarioMes(String titulo, int largura, int altura) {
        super(titulo, largura, altura);

        anoMesAtual = YearMonth.now();

        configurarComponentes();//configuração da tela (especificamente para essa)

        setVisible(true);
        
    }

    @Override
    protected void configurarComponentes() {
        setLayout(new BorderLayout());

        titulo = new JLabel("", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH); 

        JPanel painelBotoes = new JPanel();

        JButton btnAnterior = new JButton("◀");
        btnAnterior.setFocusable(false);
        btnAnterior.setBackground(new Color(210, 210, 225));
        btnAnterior.setFocusPainted(false);


        JButton btnProximo = new JButton("▶");
        btnProximo.setFocusable(false);
        btnProximo.setBackground(new Color(210, 210, 225));
        btnProximo.setFocusPainted(false);

        //Adiciona os botões no painel, embaixo
        painelBotoes.add(btnAnterior);
        painelBotoes.add(btnProximo);
        add(painelBotoes, BorderLayout.SOUTH);

        panelDias = new JPanel(new GridLayout(0, 7));
        add(panelDias, BorderLayout.CENTER);

        // Ações dos botões
        btnAnterior.addActionListener(e -> {
            anoMesAtual = anoMesAtual.minusMonths(1);
            atualizarCalendario();
        });

        btnProximo.addActionListener(e -> {
            anoMesAtual = anoMesAtual.plusMonths(1);
            atualizarCalendario();
        });

        atualizarCalendario();

    }

    private void atualizarCalendario() {
        panelDias.removeAll();

        LocalDate hoje = LocalDate.now();
        int diasNoMes = anoMesAtual.lengthOfMonth();
        LocalDate primeiroDia = anoMesAtual.atDay(1);
        int diaDaSemana = primeiroDia.getDayOfWeek().getValue(); // 1=Segunda, 7=Domingo
        String[] diasSemana = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};

        // Cores
        Color azulCabecalho = new Color(70, 130, 180);
        Color vermelhoSab = new Color(255, 230, 230);
        Color hojeColor = new Color(220, 245, 220);
        Color domingColor = Color.LIGHT_GRAY;

        for(String dia : diasSemana){//cria cabeçalho
            JLabel labelDia = new JLabel(dia, SwingConstants.CENTER);
            labelDia.setOpaque(true);
            if(dia.equals("Sab")){
                labelDia.setBackground(vermelhoSab);//troca a cor do dia se for sábado
                labelDia.setForeground(Color.RED);
            }else{
                labelDia.setBackground(azulCabecalho);
                labelDia.setForeground(Color.WHITE);
            }
            labelDia.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            labelDia.setFont(new Font("Arial", Font.BOLD, 14));
            panelDias.add(labelDia);
        }

        int espacovazio = primeiroDia.getDayOfWeek().getValue()%7;//Quantidade espaço vazio
        for(int i=0; i<espacovazio;i++){
            panelDias.add(new JLabel("")); // Espaço vazio
        }

        for(int dia = 1; dia<=diasNoMes; dia++){
            final int diabotao = dia;
            JButton btnDia = new JButton(String.valueOf(diabotao));
            btnDia.setFocusPainted(false);
            btnDia.setFont(new Font("Arial", Font.PLAIN, 12));

            boolean diaAtual = dia == hoje.getDayOfMonth() && //Ver se o dia gerado é o mesmo dia que estamos
                                anoMesAtual.getMonth() == hoje.getMonth() && 
                                anoMesAtual.getYear() == hoje.getYear();

            int diaSemanaAtual = (diaDaSemana + dia-1) % 7;//// 1=Segunda, 6=Sábado, 0=Domingo

            if(diaAtual){
                btnDia.setBackground(hojeColor);
                btnDia.setForeground(Color.green);
                btnDia.setFont(new Font("Arial", Font.BOLD, 14));
            } else if(diaSemanaAtual == 0){
                btnDia.setBackground(domingColor);
                btnDia.setForeground(Color.BLACK);
            } else if(diaSemanaAtual == 6){
                btnDia.setBackground(vermelhoSab);
                btnDia.setForeground(Color.red);
            } else {
                btnDia.setBackground(Color.WHITE);
                btnDia.setForeground(Color.BLACK);  
            }

            btnDia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));

            btnDia.addActionListener(e-> tarefa(primeiroDia, diabotao));

            panelDias.add(btnDia);
        }

        titulo.setText(mesesIdx[anoMesAtual.getMonthValue()-1] + " " + anoMesAtual.getYear());

        panelDias.revalidate();
        panelDias.repaint();
    }

    public void tarefa(LocalDate a, int b){
        Tarefa c = new Tarefa(a, b);
    }
    
}
