/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import connection.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author Thiago
 */
public class TelaGestaoClientes extends JFrame {
    private JTextField txtNome, txtCPF, txtTelefone;
    private JButton btnSalvar, btnRemover; // Adicione btnRemover aqui
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public TelaGestaoClientes () {
        
        setTitle("Cadastro de Cliente");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.NORTH);

        criarTabela();
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);

        carregarClientes();
        
        setVisible(true);

    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtNome = new JTextField();
        txtCPF = new JTextField();
        txtTelefone = new JTextField();
        btnSalvar = new JButton("Salvar");
        btnRemover = new JButton("Remover"); // Adicione o botão remover

        btnSalvar.addActionListener(e -> {
            salvarCliente();
            carregarClientes();
            limparCampos();
        });

        btnRemover.addActionListener(e -> {
            removerCliente();
            carregarClientes();
        });
        
        // Adicionando os componentes ao painel
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("CPF:"));
        painel.add(txtCPF);
        painel.add(new JLabel("Telefone:"));
        painel.add(txtTelefone);
        painel.add(btnRemover); // Adicione o botão remover ao painel
        painel.add(btnSalvar);


        return painel;
    }
    private void criarTabela() {
        modeloTabela = new DefaultTableModel(
            new String[] {"ID", "Nome", "CPF", "Telefone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede edição direta na tabela
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite apenas uma seleção
    }
    private void carregarClientes() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clientes")) {
                while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("CPF"),
                    rs.getString("Telefone"),
                } );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void salvarCliente() {
        try (Connection con = Conexao.getConexao()) {
            String sql = "INSERT INTO clientes (Nome, CPF, Telefone) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            
            // Prepara os dados para inserção
            stmt.setString(1, txtNome.getText());
            stmt.setString(2, txtCPF.getText());
            stmt.setString(3, txtTelefone.getText());
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente: " + ex.getMessage());
        }
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtCPF.setText("");
        txtTelefone.setText("");
    }
    
    private void removerCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um cliente para remover.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja remover este cliente?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try (Connection con = Conexao.getConexao()) {
                int idCliente = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                String sql = "DELETE FROM clientes WHERE ID = ?";
                
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, idCliente);
                
                int linhasAfetadas = stmt.executeUpdate();
                
                if (linhasAfetadas > 0) {
                    JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
                    carregarClientes(); // Recarrega a tabela
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível remover o cliente.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover cliente: " + ex.getMessage());
            }
        }
    }
}