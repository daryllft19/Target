import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class TargetFrame implements Constants{
	Handler handler = new Handler();
	
	public TargetFrame(int w, int h, Game game){
		
		game.setPreferredSize(new Dimension(w,h));
		game.setMaximumSize(new Dimension(w,h));
		game.setMinimumSize(new Dimension(w,h));
		
		JFrame frame = new JFrame(APP_NAME);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
				if(JOptionPane.showConfirmDialog(null,
					"Are you sure?","Close Client",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					System.exit(0);	
				}		
			}
		});
		
		game.start(frame);
	}


}