package sound;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Vector;



public class sound extends JPanel
implements Runnable, LineListener, MetaEventListener, ActionListener
{
    public Vector sounds = new Vector();
    private Thread thread;
    private Sequencer sequencer;
    private boolean midiEOM, audioEOM; 
    private Synthesizer synthesizer; 
    private MidiChannel channels[]; 
    private Object currentSound; 
    private String currentName; 
    private int num = -1;
    private boolean bump;
    private boolean paused = false;
    public boolean loop = true;
    private int volumn = 100;

    JButton startB, pauseB, prevB, nextB;    /** Control Buttons **/


    public sound(String dirName)
    {
        if (dirName != null)
        {
            loadFile(dirName);  /** lade die Sound Datei **/
        }


        JPanel jp = new JPanel(new GridLayout(4, 1));
        /** erstellt die Buttons */
        startB = addButton("Start", jp, sounds.size() != 0);
        pauseB = addButton("Pause", jp, false);
        prevB = addButton("<<", jp, false);
        nextB = addButton(">>", jp, false);
    }


    private JButton addButton(String name, JPanel panel, boolean state)
    {
        
        JButton b = new JButton(name);
        
        b.addActionListener(this);
        
        b.setEnabled(state);
        if (panel != null)
            
            panel.add(b);
        
        return b;
    }

    /** erstellt und �ffnet den sequencer **/
    public void open()
    {
        try
        {
            
            sequencer = MidiSystem.getSequencer();
            if (sequencer instanceof Synthesizer)
            {
                synthesizer = (Synthesizer)sequencer;
                
                channels = synthesizer.getChannels();
            }
        }
        catch (Exception e)
        {
            return;
        }
        
        sequencer.addMetaEventListener(this);
    }

    /** schlie�e den sequencer **/
    public void close()
    {
        /** wenn ein sound grade spielt **/
        if (thread != null && startB != null)
        {
            /** den sound stoppen **/
            startB.doClick(0);
        }
        /** wenn der sequencer ge�ffnet ist **/
        if (sequencer != null)
        {
            /** schlie�en **/
            sequencer.close();
        }
    }

	
    /** l�d alle Sound Dateien in ein Verzeichnis **/
    public void loadFile(String name)
    {
        try
        {
            
            File file = new File(name);
            
            if (file != null && file.isDirectory())
            {
                /** bekomme die Liste von den Dateien im Verzeichnis **/
                String files[] = file.list();
                /** wiederhole durch die Liste **/
                for (int i = 0; i < files.length; i++)
                {
                    /** bekomme den Pfad **/
                    File leafFile = new File(file.getAbsolutePath(), files[i]);
                    /** wenn es ein Verzeichnis ist **/
                    if (leafFile.isDirectory())
                    {
                        loadFile(leafFile.getAbsolutePath());
                    }
                    /** wenn es eine Datei ist **/
                    else
                    {
                        /** lade die Datei **/
                        addSound(leafFile);
                    }
                }
            }
            /** wenn das Objekt eine Datei ist und es existiert **/
            else if (file != null && file.exists())
            {
                /** lade die Datei **/
                addSound(file);
            }
        }
        catch (Exception e)
        {
        }
    }


    private void addSound(File file)
    {
        String s = file.getName();
        
        if (s.endsWith(".au") || s.endsWith(".rmf") ||
        s.endsWith(".mid") || s.endsWith(".wav") ||
        s.endsWith(".aif") || s.endsWith(".aiff"))
        {
            sounds.add(file);
        }
    }


    public boolean loadSound(Object object)
    {
        if (object instanceof File)
        {
            
            currentName = ((File) object).getName();
            try
            {
                
                currentSound = AudioSystem.getAudioInputStream((File) object);
            }
            
        }

        
        if (sequencer == null)
        {
            
            currentSound = null;
            
            return false;
        }

        
        if (currentSound instanceof AudioInputStream)
        {
            try
            {
                
                AudioInputStream stream = (AudioInputStream) currentSound;
                
                AudioFormat format = stream.getFormat();

                
                if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW))
                {
                    
                    AudioFormat tmp = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(), format.getSampleSizeInBits() * 2,
                    format.getChannels(), format.getFrameSize() * 2,
                    format.getFrameRate(), true);
                    
                    stream = AudioSystem.getAudioInputStream(tmp, stream);
                    
                    format = tmp;
                }
                
                DataLine.Info info = new DataLine.Info( Clip.class,
                stream.getFormat(), ((int) stream.getFrameLength() *
                format.getFrameSize()));

                
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.addLineListener(this);
                clip.open(stream);
                
                currentSound = clip;
            }

        }
        
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            try
            {
                
                sequencer.open();
                
                if (currentSound instanceof Sequence)
                {
                    
                    sequencer.setSequence((Sequence) currentSound);
                }
                
                else
                {
                    
                    sequencer.setSequence((BufferedInputStream) currentSound);
                }
            }
            catch (InvalidMidiDataException imde)
            {
                return false;
            }
            catch (Exception ex)
            {
                return false;
            }
        }
        
        return true;
    }

    /** spielt das derzeitige Sound Objekt **/
    public void playSound() {
        
        setGain(volumn);
        setPan();
        
        midiEOM = audioEOM = bump = false;
        
        if (currentSound instanceof Sequence
        || currentSound instanceof BufferedInputStream && thread != null)
        {
            
            sequencer.start();
            
            while (!midiEOM && thread != null && !bump)
            {
                try
                {
                    thread.sleep(99);
                }
                
            }
            
            sequencer.stop();
            
            sequencer.close();
        }
        


    public void update(LineEvent event)
    {
        if (event.getType() == LineEvent.Type.STOP && !paused)
        {
            audioEOM = true;
        }
    }


    public void meta(MetaMessage message)
    {
        if (message.getType() == 47)
        {
            midiEOM = true;
        }
    }


    public Thread getThread()
    {
        return thread;
    }



    public void start()
    {
        thread = new Thread(this);
        
        thread.setName("SoundPlayer");
        
        thread.start();
    }


    public void stop()
    {
        
        if (thread != null)
        {
            
            thread.interrupt();
        }
        
        thread = null;
    }


    public void run() {
        do
        {
            
            if( loadSound(sounds.elementAt(num)) == true )
            {
                
                playSound();
            }
        }
        
        while (loop && thread != null);

        
        if (thread != null)
        {
            
            startB.doClick();
        }
        
        thread = null;
        currentName = null;
        currentSound = null;
    }


    public void setPan() {
        
        int value = 0;
        
        if (currentSound instanceof Clip)
        {
            try
            {
                
                Clip clip = (Clip) currentSound;
                
                FloatControl panControl =
                (FloatControl) clip.getControl(FloatControl.Type.PAN);
                
                panControl.setValue(value/100.0f);
            }
            catch (Exception ex)
            {
            }
        }
        
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            
            for (int i = 0; i < channels.length; i++)
            {
                
                channels[i].controlChange(10,
                (int)(((double)value + 100.0) / 200.0 *  127.0));
            }
        }
    }


    public void setGain(double value)
    {
        
        if (currentSound instanceof Clip)
        {
            try
            {
                
                Clip clip = (Clip) currentSound;
                
                FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                float dB = (float)
                (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
                
                gainControl.setValue(dB);
            }
            catch (Exception ex)
            {
            }
        }
        
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            
            for (int i = 0; i < channels.length; i++)
            {
                
                channels[i].controlChange(7, (int)(value * 127.0));
            }
        }
    }


    public void mute()
    {
        volumn = 0;
        setGain(volumn);
        
        bump = true;
    }


    public void unmute()
    {
        volumn = 100;
        setGain(volumn);
        
        bump = true;
    }


    public void change(int n, boolean l)
    {
        paused = false;
        
        pauseB.setText("Pause");
        
        loop = l;
        num = n;
        
        bump = true;
        
        if (startB.getText().equals("Start"))
            
            startB.doClick();
    }


    public void controlPlay()
    {
        startB.doClick();
    }


    public void controlStop()
    {
        startB.doClick();
    }


    public void controlBack()
    {
        prevB.doClick();
    }


    public void controlNext()
    {
        nextB.doClick();
    }


    public void setComponentsEnabled(boolean state)
    {
        pauseB.setEnabled(state);
        prevB.setEnabled(state);
        nextB.setEnabled(state);
    }


     public boolean isPlaying() {
        return (startB.getText().equals("Start") ? false : true);
     }


    public void actionPerformed(ActionEvent e)
    {
        
        JButton button = (JButton) e.getSource();
        
        if (button.getText().equals("Start"))
        {
            
            paused = false;
            
            num = num == -1 ? 0 : num;
            
            start();
            
            button.setText("Stop");
            
            setComponentsEnabled(true);
        }
        /** wenn der stop button geklickt wurde **/
        else if (button.getText().equals("Stop"))
        {
            
            paused = false;
            
            stop();
            
            button.setText("Start");
            pauseB.setText("Pause");
            
            setComponentsEnabled(false);
			
        }
        
        else if (button.getText().equals("Pause"))
        {
            
            paused = true;
            
            if (currentSound instanceof Clip)
            {
                
                ((Clip) currentSound).stop();
            }
            
            else if (currentSound instanceof Sequence ||
            currentSound instanceof BufferedInputStream)
            {
                
                sequencer.stop();
            }
            
            pauseB.setText("Resume");
        }
        else if (button.getText().equals("Resume"))
        {
            
            paused = false;
            
            if (currentSound instanceof Clip)
            {
                
                ((Clip) currentSound).start();
            }
            
            else if (currentSound instanceof Sequence ||
            currentSound instanceof BufferedInputStream)
            {
                
                sequencer.start();
            }
            
            pauseB.setText("Pause");
        }
        
        else if (button.getText().equals("<<"))
        {
            paused = false;
            
            pauseB.setText("Pause");
            
            num = num-1 < 0 ? sounds.size()-1 : num-2;
            
            bump = true;
        }
        /** wenn der n�chste button geklickt wurde */
        else if (button.getText().equals(">>"))
        {
            paused = false;
            /** �ndere button Text */
            pauseB.setText("Pause");
            /** gehe zum n�chsten sound */
            num = num+1 == sounds.size() ? -1 : num;
            /** �ndere sound */
            bump = true;
        }
    }
}