import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class TargetServerFrame implements Constants{
	
	public TargetServerFrame(int w, int h, GameServer gameServer){
		
		
		JFrame frame = new JFrame(APP_NAME+": Server");
		frame.add(gameServer);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		final GameServer gameServerFinal = gameServer;
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
				if(JOptionPane.showConfirmDialog(null,
					"Are you sure? All clients would not work.","Close Server",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					gameServerFinal.broadcast("DISCONNECT<delimiter>SERVER");
					System.exit(0);	
				}		
			}
		});
		
		gameServer.start();
	}


}