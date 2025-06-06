package resources.interface_card;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ClienteVenda extends JPanel {
    private final Dotenv dotenv;
    private final ArrayList<String> cpfs = new ArrayList<>();
    private final ArrayList<String> nomes = new ArrayList<>();
    private final ArrayList<String> datas = new ArrayList<>();
    private final ArrayList<String> ids = new ArrayList<>();
    private final HashMap<String, Integer> nomeIndexMap = new HashMap<>();
    private final HashMap<String, Integer> cpfIndexMap = new HashMap<>();
    private final HashMap<String, Integer> idIndexMap = new HashMap<>();
    private final Frame framePrincipal;
    private Client client = new Client();

    public ClienteVenda(Frame frame) {
        this.dotenv = Dotenv.load();
        this.framePrincipal = frame;
        initComponents();
        getClient();
    }

    private void saveClient(int index) {
        this.client.setId(ids.get(index));
        this.client.setName(jComboBoxName.getSelectedItem().toString());
        this.client.setData(jTextFieldDataNascimento.getText().toString());
        this.client.setPayment(jComboBoxMetodoPagamento.getSelectedItem().toString());
    }

    private void updatePayment(){
        this.client.setPayment(jComboBoxMetodoPagamento.getSelectedItem().toString());
    }

    public Client getClientDAO(){
        return this.client;
    }

    public void resetCLient(){
        this.client.reset();
        jComboBoxName.setSelectedIndex(0);
        jTextFieldDataNascimento.setText("");
        jComboBoxMetodoPagamento.setSelectedIndex(0);
        jComboBoxCPF.setSelectedIndex(0);
    }

    private void initComponents() {
        // inicialização de variáveis
        jLabelCPF = new JLabel();
        jLabelDataNascimento = new JLabel();
        jLabelNome = new JLabel();
        jLabelFormaPagamento = new JLabel();

        try{
            jTextFieldDataNascimento = new javax.swing.JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jPanelContent = new JPanel(new GridBagLayout());

        jComboBoxName = new JComboBox<String>();
        jComboBoxCPF = new JComboBox<String>();
        jComboBoxMetodoPagamento = new JComboBox<String>();

        gbc = new GridBagConstraints();

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 300));
        setLayout(new FlowLayout(FlowLayout.CENTER,30,30));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Dimension fieldSize = new Dimension(400, 40);

        jPanelContent.setPreferredSize(new Dimension(1200, 450));
        jPanelContent.setBackground(Color.WHITE);
        jPanelContent.setBorder(BorderFactory.createEmptyBorder(-150, 10, 10, 10));

        jLabelNome.setFont(new Font("Cormorant Garamond", 1, 18));
        jLabelNome.setText("Nome:");
        jLabelNome.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 100);
        jPanelContent.add(jLabelNome, gbc);

        jComboBoxName.setBackground(new Color(255, 255, 255));
        jComboBoxName.setBorder(new MatteBorder(2, 2, 2, 2, new Color(128, 0, 32)));
        jComboBoxName.setFont(new Font("Cormorant Garamond", 1, 18));
        jComboBoxName.setPreferredSize(fieldSize);
        jComboBoxName.setForeground(Color.BLACK);
        jComboBoxName.setEditable(true);
        JTextComponent editor = (JTextComponent) jComboBoxName.getEditor().getEditorComponent();

        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = editor.getText().toLowerCase();
                jComboBoxName.removeAllItems();

                for (String nome : nomes) {
                    if (nome.toLowerCase().contains(text)) {
                        jComboBoxName.addItem(nome);
                    }
                }

                editor.setText(text);
                editor.setForeground(Color.BLACK);
                jComboBoxName.showPopup();
            }
        });

        jComboBoxName.addItemListener(evt -> {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                String nomeSelecionado = (String) jComboBoxName.getSelectedItem();
                Integer index = nomeIndexMap.get(nomeSelecionado);

                if (index != null) {
                    jComboBoxCPF.setSelectedItem(cpfs.get(index));
                    jTextFieldDataNascimento.setText(datas.get(index));
                    this.saveClient(index);
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 100);
        jPanelContent.add(jComboBoxName, gbc);

        jLabelDataNascimento.setFont(new Font("Cormorant Garamond", 1, 18));
        jLabelDataNascimento.setText("Data de Nascimento:");
        jLabelDataNascimento.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        jPanelContent.add(jLabelDataNascimento, gbc);

        jTextFieldDataNascimento.setBackground(new Color(255, 255, 255));
        jTextFieldDataNascimento.setBorder(new MatteBorder(2, 2, 2, 2, new Color(128, 0, 32)));
        jTextFieldDataNascimento.setFont(new Font("Cormorant Infant", 1, 18));
        jTextFieldDataNascimento.setPreferredSize(fieldSize);
        jTextFieldDataNascimento.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        jPanelContent.add(jTextFieldDataNascimento, gbc);

        jLabelCPF.setFont(new Font("Cormorant Garamond", 1, 18));
        jLabelCPF.setText("CPF:");
        jLabelCPF.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 100);
        jPanelContent.add(jLabelCPF, gbc);

        jComboBoxCPF.setBackground(Color.WHITE);
        jComboBoxCPF.setFont(new Font("Cormorant Infant", Font.BOLD, 18));
        jComboBoxCPF.setBorder(new MatteBorder(2, 2, 2, 2, new Color(128, 0, 32)));
        jComboBoxCPF.setPreferredSize(fieldSize);
        jComboBoxCPF.setForeground(Color.BLACK);
        jComboBoxCPF.setEditable(true);
        JTextComponent editorCpf = (JTextComponent) jComboBoxCPF.getEditor().getEditorComponent();

        editorCpf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = editorCpf.getText();
                jComboBoxCPF.removeAllItems();

                for (String cpf : cpfs) {
                    if (cpf.contains(text)) {
                        jComboBoxCPF.addItem(cpf);
                    }
                }

                editorCpf.setText(text);
                editorCpf.setForeground(Color.BLACK);
                jComboBoxCPF.showPopup();
            }
        });

        jComboBoxCPF.addItemListener(evt -> {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                String cpfSelecionado = (String) jComboBoxCPF.getSelectedItem();
                Integer index = cpfIndexMap.get(cpfSelecionado);

                if (index != null) {
                    jComboBoxName.setSelectedItem(nomes.get(index));
                    jTextFieldDataNascimento.setText(datas.get(index));
                    this.saveClient(index);
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 100);
        jPanelContent.add(jComboBoxCPF, gbc);

        jLabelFormaPagamento.setFont(new Font("Cormorant Garamond", 1, 18));
        jLabelFormaPagamento.setText("Forma de Pagamento:");
        jLabelFormaPagamento.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 100);
        jPanelContent.add(jLabelFormaPagamento, gbc);

        jComboBoxMetodoPagamento.setBackground(Color.WHITE);
        jComboBoxMetodoPagamento.setForeground(Color.BLACK);
        jComboBoxMetodoPagamento.setFont(new Font("Cormorant Garamond", Font.BOLD, 18));
        jComboBoxMetodoPagamento.setBorder(new MatteBorder(2, 2, 2, 2, new Color(128, 0, 32)));
        jComboBoxMetodoPagamento.setPreferredSize(fieldSize);
        jComboBoxMetodoPagamento.addItem("CARTÃO_DE_CRÉDITO");
        jComboBoxMetodoPagamento.addItem("CARTÃO_DE_DÉBITO");
        jComboBoxMetodoPagamento.addItem("DINHEIRO");
        jComboBoxMetodoPagamento.addItem("PIX");
        jComboBoxMetodoPagamento.addItem("TRANSFERÊNCIA_BANCÁRIA");
        jComboBoxMetodoPagamento.addItemListener(evt->updatePayment());
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        jPanelContent.add(jComboBoxMetodoPagamento, gbc);

        add(jPanelContent);
    }

    private void getClient() {
        try {
            String urlAPI = this.dotenv.get("API_HOST");
            URL url = new URL(urlAPI + "/client");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonArray arrClient = JsonParser.parseString(response.toString()).getAsJsonArray();
                DefaultComboBoxModel<String> model_nome = new DefaultComboBoxModel<>();
                DefaultComboBoxModel<String> model_cpf = new DefaultComboBoxModel<>();

                nomes.clear();
                cpfs.clear();
                datas.clear();
                nomeIndexMap.clear();
                cpfIndexMap.clear();
                idIndexMap.clear();

                int index = 0;
                for (JsonElement arr : arrClient) {
                    JsonObject arrNome = arr.getAsJsonObject();
                    String nome = arrNome.get("name").getAsString();
                    String cpf = arrNome.get("cpf").getAsString();
                    String dataNascimento = arrNome.get("dateBirth").getAsString();
                    String id = arrNome.get("id").getAsString();

                    nomes.add(nome);
                    cpfs.add(cpf);
                    datas.add(dataNascimento);
                    ids.add(id);
                    model_nome.addElement(nome);
                    model_cpf.addElement(cpf);

                    nomeIndexMap.put(nome, index);
                    cpfIndexMap.put(cpf, index);
                    idIndexMap.put(id,index);
                    index++;
                }

                jComboBoxName.setModel(model_nome);
                jComboBoxCPF.setModel(model_cpf);
                connection.disconnect();
            } else {
                JOptionPane.showMessageDialog(framePrincipal, "Erro ao carregar o cliente: " + responseCode);
                connection.disconnect();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(framePrincipal, e.getMessage());
        }
    }

    public void cadastrarClient(){
        CadastroClienteDialog dialog = new CadastroClienteDialog(this.framePrincipal);
        dialog.setVisible(true);
        if(!dialog.isVisible()){
            boolean result = dialog.getResult();
            if(result){
                getClient();
            }
        }
    }

    private JLabel jLabelCPF;
    private JLabel jLabelNome;
    private JLabel jLabelDataNascimento;
    private JLabel jLabelFormaPagamento;

    private JComboBox<String> jComboBoxName;
    private JComboBox<String> jComboBoxCPF;
    private JComboBox<String> jComboBoxMetodoPagamento;

    private JTextField jTextFieldDataNascimento;

    private JPanel jPanelContent;

    private GridBagConstraints gbc;
}
