import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

public class Tarefa extends CriaTela {

    private static Map<LocalDate, List<Task>> agendaMap = new HashMap<>();
    private LocalDate data;
    private List<Task> listaAgenda;
    private DefaultListModel<Task> listModel;
    private Runnable refreshList;
    private JList<Task> taskList;
    private JTextField inputAgenda;
    private JComboBox<Prioridade> boxPrioridade;

    public Tarefa(LocalDate data, int diaAtual) {
        super("Agenda dia '" + FormatDia(data, diaAtual) + "'", 400, 500);
        
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

        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputAgenda = new JTextField(15);

        boxPrioridade = new JComboBox<>(Prioridade.values());
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

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Task task = listModel.get(index);
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
            Task task = new Task(texto, prioridade);
            listaAgenda.add(task);
            refreshList.run();
            saveAgenda();
        }
    }

    private void removerTarefa() {
        int idx = taskList.getSelectedIndex();
        if (idx >= 0) {
            Task selected = listModel.get(idx);
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
            agendaMap = (Map<LocalDate, List<Task>>) ois.readObject();
        } catch (Exception e) {
            agendaMap = new HashMap<>();
        }
    }
}
