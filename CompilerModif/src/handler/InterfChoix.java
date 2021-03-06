package handler;
import struct.*;
import java.util.ArrayList;
import java.awt.*;

public class InterfChoix extends javax.swing.JFrame {

	private Lattice latt;
	private ArrayList<Lat_Concept> favoriteConceptList;
	//private Interf inter;
	
	public ArrayList<Lat_Concept> getfavoriteConceptList(){
		return(favoriteConceptList);
	}
	public void setfavoriteConceptList(ArrayList<Lat_Concept> tmp){
		favoriteConceptList=tmp;
	}
	
	public InterfChoix(Lattice l,ArrayList<Lat_Concept> favoriteConceptList) {
    	this.latt=l;
    	this.favoriteConceptList = favoriteConceptList;
        initComponents();
        setVisible(true);
	}

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

    	buttonGroup1 = new javax.swing.ButtonGroup();
    	jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jop1 = new javax.swing.JOptionPane();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Choix du critère");

        jLabel2.setText("Chevauchement minimal");

        jLabel3.setText("Latence minimale (Taille des clés des index minimale)");

        jButton1.setText("OK");
        
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButton1)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2))))
                .addGap(24, 24, 24))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButton1, jRadioButton2});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jLabel1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel2))
                    .addComponent(jRadioButton1))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jRadioButton2))
                .addGap(21, 21, 21)
                .addComponent(jButton1)
                .addGap(31, 31, 31))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jRadioButton2});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel3});

        pack();
    }// </editor-fold>                        

   
                                                 
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {  
    	if (jRadioButton1.isSelected()) {
    		LatticeHandler latticeHandler = new LatticeHandler();
        	this.setfavoriteConceptList(latticeHandler.getSignificantConcept(this.latt));
        	if (!favoriteConceptList.isEmpty()){
        	jop1.showMessageDialog(null, "Opération terminée avec succès ! ", "Information", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            dispose();
            } 
    	}
       if (jRadioButton2.isSelected()) {
    		Interf inter = new Interf (this," ",true,latt,favoriteConceptList);
    		inter.setVisible(true);
    		this.setfavoriteConceptList(inter.getfavoriteConceptList());
    		setVisible(false);
            dispose();
            
    	}
    	
    }
    

    // Variables declaration - do not modify   
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JOptionPane jop1;
    // End of variables declaration                   
}
