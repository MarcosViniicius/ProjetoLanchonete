package views;

import dao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TelaGerenciamentoPedidos extends JFrame {
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    private JButton btnAlterarStatus, btnAdicionarPedido;
    private JComboBox<Integer> comboCliente, comboProduto;
    private JTextField campoQuantidade, campoValorTotal;

    public TelaGerenciamentoPedidos() {
        setTitle("Gerenciamento de Pedidos");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "ID_Cliente", "ID_Produto", "Quantidade", "Valor_Total", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPedidos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaPedidos);

        // Painel para adicionar pedido
        JPanel painelAdicionar = new JPanel(new FlowLayout());
        painelAdicionar.add(new JLabel("Cliente (ID):"));
        comboCliente = new JComboBox<>();
        painelAdicionar.add(comboCliente);

        painelAdicionar.add(new JLabel("Produto (ID):"));
        comboProduto = new JComboBox<>();
        painelAdicionar.add(comboProduto);

        painelAdicionar.add(new JLabel("Quantidade:"));
        campoQuantidade = new JTextField(5);
        painelAdicionar.add(campoQuantidade);

        painelAdicionar.add(new JLabel("Valor Total:"));
        campoValorTotal = new JTextField(7);
        painelAdicionar.add(campoValorTotal);

        btnAdicionarPedido = new JButton("Adicionar Pedido");
        painelAdicionar.add(btnAdicionarPedido);

        btnAlterarStatus = new JButton("Alterar Status");
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnAlterarStatus);

        add(scrollPane, BorderLayout.CENTER);
        add(painelAdicionar, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAlterarStatus.addActionListener(e -> alterarStatusPedido());
        btnAdicionarPedido.addActionListener(e -> adicionarPedido());

        carregarPedidos();
        carregarClientes();
        carregarProdutos();
        setVisible(true);
    }

    private void carregarPedidos() {
        modeloTabela.setRowCount(0);
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT * FROM Pedidos";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("ID"),
                        rs.getInt("ID_Cliente"),
                        rs.getInt("ID_Produto"),
                        rs.getInt("Quantidade"),
                        rs.getDouble("Valor_Total"),
                        rs.getString("Status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos:\n" + ex.getMessage());
        }
    }

    private void carregarClientes() {
        comboCliente.removeAllItems();
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT ID FROM Clientes";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboCliente.addItem(rs.getInt("ID"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes:\n" + ex.getMessage());
        }
    }

    private void carregarProdutos() {
        comboProduto.removeAllItems();
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT ID FROM Produtos";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboProduto.addItem(rs.getInt("ID"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos:\n" + ex.getMessage());
        }
    }

    private void adicionarPedido() {
        Integer idCliente = (Integer) comboCliente.getSelectedItem();
        Integer idProduto = (Integer) comboProduto.getSelectedItem();
        String quantidadeStr = campoQuantidade.getText().trim();
        String valorTotalStr = campoValorTotal.getText().trim();

        if (idCliente == null || idProduto == null || quantidadeStr.isEmpty() || valorTotalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos para adicionar o pedido.");
            return;
        }

        int quantidade;
        double valorTotal;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
            valorTotal = Double.parseDouble(valorTotalStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade e Valor Total devem ser numéricos.");
            return;
        }

        try (Connection con = Conexao.getConexao()) {
            String sql = "INSERT INTO Pedidos (ID_Cliente, ID_Produto, Quantidade, Valor_Total, Status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idProduto);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, valorTotal);
            stmt.setString(5, "Novo");
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pedido adicionado com sucesso!");
            carregarPedidos();
            campoQuantidade.setText("");
            campoValorTotal.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar pedido:\n" + ex.getMessage());
        }
    }

    private void alterarStatusPedido() {
        int row = tabelaPedidos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para alterar o status.");
            return;
        }
        int id = (int) modeloTabela.getValueAt(row, 0);
        String statusAtual = (String) modeloTabela.getValueAt(row, 5);

        String novoStatus = JOptionPane.showInputDialog(this, "Novo status:", statusAtual);
        if (novoStatus == null || novoStatus.trim().isEmpty()) {
            return;
        }

        try (Connection con = Conexao.getConexao()) {
            String sql = "UPDATE Pedidos SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);
            int atualizado = stmt.executeUpdate();
            if (atualizado > 0) {
                JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!");
                carregarPedidos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar status.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaGerenciamentoPedidos::new);
    }
}