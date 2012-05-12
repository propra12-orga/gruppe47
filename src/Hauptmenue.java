/**
 * 
 * @author Timon Günther
 *
 */
	public class Hauptmenue extends javax.swing.JFrame {
		
	    public Hauptmenue() {
	        initComponents();
	    }
	    private void initComponents() {

	        jPanel2 = new javax.swing.JPanel();
	        jLabel1 = new javax.swing.JLabel();
	        jButton4 = new javax.swing.JButton();
	        jButton5 = new javax.swing.JButton();
	        jButton6 = new javax.swing.JButton();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setTitle("Bomberman");
	        setBackground(new java.awt.Color(255, 255, 255));
	        setBounds(new java.awt.Rectangle(0, 0, 500, 500));
	        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
	        setPreferredSize(new java.awt.Dimension(1280, 1024));
	        setResizable(false);

	        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
	        jPanel2.setPreferredSize(new java.awt.Dimension(1280, 1024));

	        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
	        jLabel1.setIcon(new javax.swing.ImageIcon("H:\\logo bomberman.png")); // NOI18N

	        jButton4.setText("Start");
	        jButton4.setMaximumSize(new java.awt.Dimension(95, 23));
	        jButton4.setMinimumSize(new java.awt.Dimension(95, 23));
	        jButton4.setPreferredSize(new java.awt.Dimension(95, 23));

	        jButton5.setText("Einstellungen");

	        jButton6.setText("Exit");
	        jButton6.setMaximumSize(new java.awt.Dimension(95, 23));
	        jButton6.setMinimumSize(new java.awt.Dimension(95, 23));
	        jButton6.setPreferredSize(new java.awt.Dimension(95, 23));

	        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	        jPanel2.setLayout(jPanel2Layout);
	        jPanel2Layout.setHorizontalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
	                .addContainerGap(350, Short.MAX_VALUE)
	                .addComponent(jLabel1)
	                .addGap(330, 330, 330))
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addGap(484, 484, 484)
	                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(0, 0, Short.MAX_VALUE))
	        );
	        jPanel2Layout.setVerticalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addGap(43, 43, 43)
	                .addComponent(jLabel1)
	                .addGap(55, 55, 55)
	                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(515, Short.MAX_VALUE))
	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(0, 0, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(501, Short.MAX_VALUE))
	        );

	        pack();
	    }
	    public static void main(String args[]) {
	        try {
	            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        } catch (ClassNotFoundException ex) {
	            java.util.logging.Logger.getLogger(Hauptmenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (InstantiationException ex) {
	            java.util.logging.Logger.getLogger(Hauptmenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (IllegalAccessException ex) {
	            java.util.logging.Logger.getLogger(Hauptmenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	            java.util.logging.Logger.getLogger(Hauptmenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
	        java.awt.EventQueue.invokeLater(new Runnable() {

	            public void run() {
	                new Hauptmenue().setVisible(true);
	            }
	        });
	    }
	    private javax.swing.JButton jButton4;
	    private javax.swing.JButton jButton5;
	    private javax.swing.JButton jButton6;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JPanel jPanel2;
	    // End of variables declaration//GEN-END:variables
	}


