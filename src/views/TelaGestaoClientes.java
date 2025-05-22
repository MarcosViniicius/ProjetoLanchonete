package views;

import dao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TelaGestaoClientes extends JFrame {
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private JTextField campoNome, campoCPF, campoTelefone;
    private JButton btnAdicionar, btnEditar, btnRemover;

    public TelaGestaoClientes() {
        setTitle("Gestão de Clientes");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Telefone"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);

        JPanel painelCampos = new JPanel(new FlowLayout());
        painelCampos.add(new JLabel("Nome:"));
        campoNome = new JTextField(10);
        painelCampos.add(campoNome);

        painelCampos.add(new JLabel("CPF:"));
        campoCPF = new JTextField(10);
        painelCampos.add(campoCPF);

        painelCampos.add(new JLabel("Telefone:"));
        campoTelefone = new JTextField(10);
        painelCampos.add(campoTelefone);

        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnRemover = new JButton("Remover");
        painelCampos.add(btnAdicionar);
        painelCampos.add(btnEditar);
        painelCampos.add(btnRemover);

        add(scrollPane, BorderLayout.CENTER);
        add(painelCampos, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnRemover.addActionListener(e -> removerCliente());

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            int row = tabelaClientes.getSelectedRow();
            if (row >= 0) {
                campoNome.setText(modeloTabela.getValueAt(row, 1).toString());
                campoCPF.setText(modeloTabela.getValueAt(row, 2).toString());
                campoTelefone.setText(modeloTabela.getValueAt(row, 3).toString());
            }
        });

        carregarClientes();
        setVisible(true);
    }

    private void carregarClientes() {
        modeloTabela.setRowCount(0);
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT * FROM Clientes";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("ID"),
                        rs.getString("Nome"),
                        rs.getString("CPF"),
                        rs.getString("Telefone")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes:\n" + ex.getMessage());
        }
    }

    private void adicionarCliente() {
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().trim();
        String telefone = campoTelefone.getText().trim();
        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }
        try (Connection con = Conexao.getConexao()) {
            String sql = "INSERT INTO Clientes (Nome, CPF, Telefone) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, telefone);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
            carregarClientes();
            campoNome.setText("");
            campoCPF.setText("");
            campoTelefone.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente:\n" + ex.getMessage());
        }
    }

    private void editarCliente() {
        int row = tabelaClientes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
            return;
        }
        int id = (int) modeloTabela.getValueAt(row, 0);
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().trim();
        String telefone = campoTelefone.getText().trim();
        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }
        try (Connection con = Conexao.getConexao()) {
            String sql = "UPDATE Clientes SET Nome = ?, CPF = ?, Telefone = ? WHERE ID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, telefone);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            carregarClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar cliente:\n" + ex.getMessage());
        }
    }

    private void removerCliente() {
        int row = tabelaClientes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.");
            return;
        }
        int id = (int) modeloTabela.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection con = Conexao.getConexao()) {
            String sql = "DELETE FROM Clientes WHERE ID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
            carregarClientes();
            campoNome.setText("");
            campoCPF.setText("");
            campoTelefone.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao remover cliente:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaGestaoClientes::new);
    }
}