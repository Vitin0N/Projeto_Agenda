import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

public class Agenda extends CriaTela {

    private static Map<LocalDate, List<Tarefa>> agendaMap = new HashMap<>();
    private LocalDate data;
    private List<Tarefa> listaAgenda;
    private DefaultListModel<Tarefa> listModel;
    private Runnable refreshList;
    private JList<Tarefa> tarefaList;
    private JTextField inputAgenda;
    private JComboBox<Prioridade> boxPrioridade;

    public Agenda(LocalDate data, int diaAtual) {
        super("Agenda dia '" + FormatDia(data, diaAtual) + "'", 400, 500);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        this.data = LocalDate.of(data.getYear(), data.getMonth(), diaAtual);
        loadAgenda();
        listaAgenda = agendaMap.computeIfAbsent(this.data, k -> new ArrayList<>());

        configurarComponentes();
        setVisible(true);
    }

    @Override
    protected void configurarComponentes() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        refreshList = () -> {
            listModel.clear();
            listaAgenda.stream()
                .sorted(Comparator.comparingInt(t -> t.getPrioridade().ordinal()))
                .forEach(listModel::addElement);
        };
        refreshList.run();

        tarefaList = new JList<>(listModel);
        tarefaList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tarefaList);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputAgenda = new JTextField(15);

        boxPrioridade = new JComboBox<>(Prioridade.values());
        boxPrioridade.setBackground(Color.LIGHT_GRAY);
        boxPrioridade.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Prioridade) {
                    setText(value.toString());
                }
                return this;
            }
        });

        inputAgenda.addActionListener(e -> adicionarTarefa());

        JButton botaoadc = new JButton("Adicionar");
        botaoadc.setFocusable(false);
        botaoadc.setFocusPainted(false);
        botaoadc.setBackground(Color.LIGHT_GRAY);
        botaoadc.addActionListener(e -> adicionarTarefa());

        JButton botaoRemove = new JButton("Remover");
        botaoRemove.setFocusable(false);
        botaoRemove.addActionListener(e -> removerTarefa());

        tarefaList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = tarefaList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Tarefa task = listModel.get(index);
                        task.setCompleto(!task.isCompleto());
                        refreshList.run();
                        saveAgenda();
                    }
                }
            }
        });

        inputPanel.add(inputAgenda);
        inputPanel.add(boxPrioridade);
        inputPanel.add(botaoadc);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(botaoRemove, BorderLayout.SOUTH);
    }

    private static String FormatDia(LocalDate data, int diaAtual) {
        LocalDate realData = LocalDate.of(data.getYear(), data.getMonth(), diaAtual);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return realData.format(formatador);
    }

    private void adicionarTarefa() {
        String texto = inputAgenda.getText().trim();
        Prioridade prioridade = (Prioridade) boxPrioridade.getSelectedItem();
        if (!texto.isEmpty()) {
            inputAgenda.setText("");
            Tarefa task = new Tarefa(texto, prioridade);
            listaAgenda.add(task);
            refreshList.run();
            saveAgenda();
        }
    }

    private void removerTarefa() {
        int idx = tarefaList.getSelectedIndex();
        if (idx >= 0) {
            Tarefa selected = listModel.get(idx);
            listaAgenda.remove(selected);
            refreshList.run();
            saveAgenda();
        }
    }

    public static void saveAgenda() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("agendateste.dat"))) {
            oos.writeObject(agendaMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadAgenda() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("agendateste.dat"))) {
            agendaMap = (Map<LocalDate, List<Tarefa>>) ois.readObject();
        } catch (Exception e) {
            agendaMap = new HashMap<>();
        }
    }
}