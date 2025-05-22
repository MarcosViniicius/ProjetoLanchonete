package views;

import dao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TelaControleEstoque extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JTextField campoQuantidade;
    private JButton btnAtualizar;

    public TelaControleEstoque() {
        setTitle("Controle de Estoque");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Categoria", "Preço", "Estoque"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);

        JPanel painelEdicao = new JPanel(new FlowLayout());
        painelEdicao.add(new JLabel("Nova quantidade em estoque:"));
        campoQuantidade = new JTextField(10);
        painelEdicao.add(campoQuantidade);

        btnAtualizar = new JButton("Atualizar Estoque");
        painelEdicao.add(btnAtualizar);

        add(scrollPane, BorderLayout.CENTER);
        add(painelEdicao, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> atualizarEstoque());

        carregarProdutos();

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            int row = tabelaProdutos.getSelectedRow();
            if (row >= 0) {
                campoQuantidade.setText(modeloTabela.getValueAt(row, 4).toString());
            }
        });

        setVisible(true);
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT * FROM produtos";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("ID"),
                        rs.getString("Nome"),
                        rs.getString("Categoria"),
                        rs.getDouble("Preco"),
                        rs.getInt("Estoque")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos:\n" + ex.getMessage());
        }
    }

    private void atualizarEstoque() {
        int row = tabelaProdutos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para atualizar.");
            return;
        }
        int id = (int) modeloTabela.getValueAt(row, 0);
        String novaQuantidadeStr = campoQuantidade.getText().trim();
        int novaQuantidade;
        try {
            novaQuantidade = Integer.parseInt(novaQuantidadeStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }

        try (Connection con = Conexao.getConexao()) {
            String sql = "UPDATE produtos SET Estoque = ? WHERE ID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, id);
            int atualizado = stmt.executeUpdate();
            if (atualizado > 0) {
                JOptionPane.showMessageDialog(this, "Estoque atualizado com sucesso!");
                carregarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar estoque.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar estoque:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaControleEstoque::new);
    }
}