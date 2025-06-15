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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 *
 * @author Blenda
 */
public class TelaGerenciamentoPedidos extends JFrame{
    // declaraçao dos componentes da interface
    private JComboBox<String> comboClientes, comboProdutos;
    private JTextField txtQuantidade, txtValorTotal;
    private JComboBox<String> comboStatus;
    private JButton btnSalvar, btnAtualizarStatus;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    
    public TelaGerenciamentoPedidos(){
        setTitle("Gerenciamento de Pedidos");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // cria e adiciona painel de formulario na parte superior
        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.NORTH);
        
        // configura tabela de pedidos
        criarTabela();
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);
        
        // carrega dados iniciais
        carregarClientesCombo();
        carregarProdutosCombo();
        carregarPedidos();
        
        setVisible(true); // torna a janela visivel
    }
    
    private JPanel criarPainelFormulario(){
        // cria painel com grid de 6 linhas e 2 colunas
        JPanel painel = new JPanel(new GridLayout(6, 2, 5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // inicializa componentes
        comboClientes = new JComboBox<>();
        comboProdutos = new JComboBox<>();
        txtQuantidade = new JTextField();
        txtValorTotal = new JTextField();
        txtValorTotal.setEditable(false); // valor total será calculado automaticamente
        
        // combobox com opçoes de status pre-definidas
        comboStatus = new JComboBox<>(new String[]{"Pendente", "Em processamento", "Enviado", "Entregue", "Cancelado"});
        
        btnSalvar = new JButton("Salvar Pedido");
        btnAtualizarStatus = new JButton("Atualizar Status");
        
        // configura açao para calcular valor total quando quantidade é alterada
        txtQuantidade.addActionListener(e -> calcularValorTotal());
        comboProdutos.addActionListener(e -> calcularValorTotal());
        txtQuantidade.addActionListener(e -> calcularValorTotal());
        txtQuantidade.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularValorTotal();
            }
        });
        
        // configura açao do botao salvar
        btnSalvar.addActionListener(e -> {
            salvarPedido();
            carregarPedidos();
            limparCampos();
        });
        
        // configura açao do botao atualizar status
        btnAtualizarStatus.addActionListener(e -> {
            atualizarStatusPedido();
            carregarPedidos();
        });
        
        // adiciona componentes ao painel com seus rotulos
        painel.add(new JLabel("Cliente:"));
        painel.add(comboClientes);
        painel.add(new JLabel("Produto:"));
        painel.add(comboProdutos);
        painel.add(new JLabel("Quantidade:"));
        painel.add(txtQuantidade);
        painel.add(new JLabel("Valor Total:"));
        painel.add(txtValorTotal);
        painel.add(new JLabel("Status:"));
        painel.add(comboStatus);
        painel.add(btnSalvar);
        painel.add(btnAtualizarStatus);
        
        return painel; // retorna painel configurado
    }
    
    private void criarTabela(){
        // cria modelo de tabela com colunas definidas
        modeloTabela = new DefaultTableModel(
                new String[] {"ID", "Cliente", "Produto", "Quantidade", "Valor Total", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column){
               return false; // torna a tabela nao editavel 
            }
        };
        tabelaPedidos = new JTable(modeloTabela);
    }
    
    private void carregarClientesCombo(){
        comboClientes.removeAllItems();
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID, Nome FROM clientes")) {
            while (rs.next()) {
                comboClientes.addItem(rs.getInt("ID") + " - " + rs.getString("Nome"));
            }
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }
    
    private void carregarProdutosCombo(){
        comboProdutos.removeAllItems();
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID, Nome, Preco FROM produtos")) {
            while (rs.next()) {
                comboProdutos.addItem(rs.getInt("ID") + " - " + rs.getString("Nome") + " (R$ " + rs.getDouble("Preco") + ")");
            }
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ex.getMessage());
            
        }   
    }
    
    private void carregarPedidos(){
        modeloTabela.setRowCount(0);
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT p.ID, c.Nome AS Cliente, pr.Nome AS Produto, " + 
                "p.Quantidade, p.Valor_Total, p.Status " +
                "FROM pedidos p " +
                "JOIN clientes c ON p.ID_Cliente = c.ID " +
                "JOIN produtos pr ON p.ID_Produto = pr.ID ")) {
            while (rs.next()){
                modeloTabela.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Cliente"),
                    rs.getString("Produto"),
                    rs.getInt("Quantidade"),
                    rs.getDouble("Valor_Total"),
                    rs.getString("Status")
                });
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + ex.getMessage());
        }
    }
    
    private void calcularValorTotal() {
        try {
            if (txtQuantidade.getText().trim().isEmpty()) {
                txtValorTotal.setText("");
                return;
            }

            String produtoSelecionado = (String) comboProdutos.getSelectedItem();
            if (produtoSelecionado != null && !produtoSelecionado.isEmpty()) {
                int start = produtoSelecionado.indexOf("(R$ ") + 4;
                int end = produtoSelecionado.indexOf(")");
                double preco = Double.parseDouble(produtoSelecionado.substring(start, end));
                
                int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
                if (quantidade <= 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.");
                    txtQuantidade.setText("");
                    txtValorTotal.setText("");
                    return;
                }
                
                double valorTotal = preco * quantidade;
                txtValorTotal.setText(String.format("%.2f", valorTotal));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido para a quantidade.");
            txtQuantidade.setText("");
            txtValorTotal.setText("");
        }
    }
    
    private void salvarPedido() {
        // Validar se campos obrigatórios estão preenchidos
        if (comboClientes.getSelectedIndex() == -1 || 
            comboProdutos.getSelectedIndex() == -1 || 
            txtQuantidade.getText().trim().isEmpty() ||
            txtValorTotal.getText().trim().isEmpty()) {  // Adiciona validação do valor total
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        try {
            // Validar e converter quantidade
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.");
                return;
            }

            // Validar e converter valor total
            String valorTotalStr = txtValorTotal.getText().trim().replace(",", ".");
            if (valorTotalStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O valor total não pode estar vazio.");
                return;
            }
            double valorTotal = Double.parseDouble(valorTotalStr);
            if (valorTotal <= 0) {
                JOptionPane.showMessageDialog(this, "O valor total deve ser maior que zero.");
                return;
            }

            // extrai IDs dos combos
            int idCliente = Integer.parseInt(((String) comboClientes.getSelectedItem()).split(" - ")[0]);
            int idProduto = Integer.parseInt(((String) comboProdutos.getSelectedItem()).split(" - ")[0]);
            String status = (String) comboStatus.getSelectedItem();
            
            try (Connection con = Conexao.getConexao();
                 PreparedStatement stmt = con.prepareStatement(
                     "INSERT INTO pedidos (ID_Cliente, ID_Produto, Quantidade, Valor_Total, Status) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setInt(1, idCliente);
                stmt.setInt(2, idProduto);
                stmt.setInt(3, quantidade);
                stmt.setDouble(4, valorTotal);
                stmt.setString(5, status);
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pedido cadastrado com sucesso!");
                limparCampos(); // Limpa os campos após salvar com sucesso
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, verifique os valores numéricos informados.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar pedido no banco de dados: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar pedido: " + ex.getMessage());
        }
    }
    
    private void atualizarStatusPedido(){
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1){
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para atualizar o status.");
            
            return;
        }
        
        int idPedido = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String novoStatus = (String) comboStatus.getSelectedItem();
        
        try (Connection con = Conexao.getConexao()){
            String sql = "UPDATE pedidos SET Status = ? WHERE ID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idPedido);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0){
                JOptionPane.showMessageDialog(this, "Status do pedido atualizado com sucesso!");
            } else{
                JOptionPane.showMessageDialog(this, "Nenhum pedido foi atualizado.");   
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status do pedido: " + ex.getMessage());
        }
    }
    
    private void limparCampos(){
        comboClientes.setSelectedIndex(0);
        comboProdutos.setSelectedIndex(0);
        txtQuantidade.setText("");
        txtValorTotal.setText("");
        comboStatus.setSelectedIndex(0);
    }
}