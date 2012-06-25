package sound;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;

import einstellung.Error;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Vector;

/**
 * This class is used to play sound files.
 *
 * @author Martin
 * @version 1.6
 */
public class sound extends JPanel
implements Runnable, LineListener, MetaEventListener, ActionListener
{
	/** sound object list */
    public Vector sounds = new Vector();
	/** thread used to play the sound */
    private Thread thread;
	/** sound sequencer */
    private Sequencer sequencer;
	/** flag: playing midi or audio file */
    private boolean midiEOM, audioEOM; 
	/** sound synthesizer */
    private Synthesizer synthesizer; 
	/** channel objects for playing midi */
    private MidiChannel channels[]; 
	/** current playing sound */
    private Object currentSound; 
	/** current sound name */
    private String currentName; 
	/** sound list offset */
    private int num = -1;
	/** flag: change of sound */
    private boolean bump;
	/** flag: paused or not */
    private boolean paused = false;
	/** flag: loop or not */
    public boolean loop = true;
	/** volumn */
    private int volumn = 100;
	/** control buttons */
    JButton startB, pauseB, prevB, nextB;    /** Control Buttons **/

	/**
     * Construct a sound player with directory name of music files.
     * @param dirName directory with sound files
     */
    public sound(String dirName)
    {
        if (dirName != null)
        {
			/** load the sound files */
            loadFile(dirName);
        }

		/** create control buttons the panel */
        JPanel jp = new JPanel(new GridLayout(4, 1));
        /** create the buttons */
        startB = addButton("Start", jp, sounds.size() != 0);
        pauseB = addButton("Pause", jp, false);
        prevB = addButton("<<", jp, false);
        nextB = addButton(">>", jp, false);
    }

	/**
     * Create a button.
     * @param name name of button
     * @param panel container of button
     * @param state enabled or disabled
     */
    private JButton addButton(String name, JPanel panel, boolean state)
    {
        /** create the button */
        JButton b = new JButton(name);
        /** add action handler */
        b.addActionListener(this);
        /** set state */
        b.setEnabled(state);
        if (panel != null)
            /** add it to the container */
            panel.add(b);
        /** return the button */
        return b;
    }

    /**
     * Create and open the sequencer.
     */
    public void open()
    {
        try
        {
            /** get the system sequencer */
            sequencer = MidiSystem.getSequencer();
            if (sequencer instanceof Synthesizer)
            {
                synthesizer = (Synthesizer)sequencer;
                /** get the system channels */
                channels = synthesizer.getChannels();
            }
        }
        catch (Exception e)
        {
            return;
        }
        /** add the meta event listener */
        sequencer.addMetaEventListener(this);
    }

    /**
     * Close the sequencer.
     */
    public void close()
    {
        /** if a sound is playing */
        if (thread != null && startB != null)
        {
            /** stop playing */
            startB.doClick(0);
        }
        /** if the sequencer is opened */
        if (sequencer != null)
        {
            /** close it */
            sequencer.close();
        }
    }
	
    /**
     * Loads all sound files ina directory.
     * @param name directory name
     */
    public void loadFile(String name)
    {
        try
        {
            /** create the file object */
            File file = new File(name);
            /** if the object is a directory */
            if (file != null && file.isDirectory())
            {
                /** get the list of files in the directory */
                String files[] = file.list();
                /** iterate through the list */
                for (int i = 0; i < files.length; i++)
                {
                    /** get the path */
                    File leafFile = new File(file.getAbsolutePath(), files[i]);
                    /** if it's a directory */
                    if (leafFile.isDirectory())
                    {
						/** recurse into the directory */
                        loadFile(leafFile.getAbsolutePath());
                    }
                    /** if it's a file */
                    else
                    {
                        /** load the file */
                        addSound(leafFile);
                    }
                }
            }
            /** if the object is a file and it exists */
            else if (file != null && file.exists())
            {
                /** load the file */
                addSound(file);
            }
        }
        catch (Exception e)
        {
        }
    }

	/**
     * Attemps to add a sound file.
     * @param file file object to add
     */
    private void addSound(File file)
    {
        String s = file.getName();
        /** if the file has right extension */
        if (s.endsWith(".au") || s.endsWith(".rmf") ||
        s.endsWith(".mid") || s.endsWith(".wav") ||
        s.endsWith(".aif") || s.endsWith(".aiff"))
        {
			/** add the file */
            sounds.add(file);
        }
    }

	/**
     * Loads a sound object.
     * @param object sound object to load
     */
    public boolean loadSound(Object object)
    {
		/** if the object is a URL object */
        if (object instanceof File)
        {
            /** get the file name */
            currentName = ((File) object).getName();
            try
            {
                /** try to create an audio sound object */
                currentSound = AudioSystem.getAudioInputStream((File) object);
            }
			/** if it's not an audio sound object */
            catch (Exception e) { new Error(e);}
        }

        /** if no sequencer available */
        if (sequencer == null)
        {
            /** set sound object to null */
            currentSound = null;
            /** and otta here */
            return false;
        }

        /** if the sound object is an AudioInputStream object */
        if (currentSound instanceof AudioInputStream)
        {
			/** create the stream */
            try
            {
                /** create the stream */
                AudioInputStream stream = (AudioInputStream) currentSound;
                /** create the file format object */
                AudioFormat format = stream.getFormat();

                /**
                 * we can't yet open the device for ALAW/ULAW playback,
                 * convert ALAW/ULAW to PCM
                 */
                if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW))
                {
                    /** setup the format */
                    AudioFormat tmp = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(), format.getSampleSizeInBits() * 2,
                    format.getChannels(), format.getFrameSize() * 2,
                    format.getFrameRate(), true);
                    /** open the stream with specified format */
                    stream = AudioSystem.getAudioInputStream(tmp, stream);
                    /** store the formst for later use */
                    format = tmp;
                }
                /** create the line to play the sound */
                DataLine.Info info = new DataLine.Info( Clip.class,
                stream.getFormat(), ((int) stream.getFrameLength() *
                format.getFrameSize()));

                /** convert the sound into a clip */
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.addLineListener(this);
                clip.open(stream);
                /** store the clip */
                currentSound = clip;
            }
            catch (Exception e) { new Error(e);}
        }
        /** if the sound object is a sequence */
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            try
            {
                /** open the sequencer */
                sequencer.open();
                /** if the object is a sequence */
                if (currentSound instanceof Sequence)
                {
                    /** set the sequence to the sequencer */
                    sequencer.setSequence((Sequence) currentSound);
                }
                /** if the object is a buffered input stream */
                else
                {
                    /** set the stream to the sequencer */
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
        /** otta here */
        return true;
    }

    /**
     * Plays the current sound object.
     */
    public void playSound() {
        /** set volumn */
        setGain(volumn);
		/** set pan */
        setPan();
        /** reset flags to false */
        midiEOM = audioEOM = bump = false;
        /** if object is a sequencer */
        if (currentSound instanceof Sequence
        || currentSound instanceof BufferedInputStream && thread != null)
        {
            /** play the sound */
            sequencer.start();
            /** iterate */
            while (!midiEOM && thread != null && !bump)
            {
                try
                {
                    thread.sleep(99);
                }
                catch (Exception e)
                {
                	break;
                }
            }
            /** stop playing */
            sequencer.stop();
            /** close the sequencer */
            sequencer.close();
        }}
        

	/**
     * Must have method.
     * @param event line event
     */
    public void update(LineEvent event)
    {
        if (event.getType() == LineEvent.Type.STOP && !paused)
        {
            audioEOM = true;
        }
    }

	/**
     * Must have method.
     * @param message meta message
     */
    public void meta(MetaMessage message)
    {
		/** 47 is end of track */
        if (message.getType() == 47)
        {
            midiEOM = true;
        }
    }

	/**
     * @return current thread
     */
    public Thread getThread()
    {
        return thread;
    }


	/**
     * Creates the thread and start it.
     */
    public void start()
    {
        /** create the thread */
        thread = new Thread(this);
        /** name the thread */
        thread.setName("SoundPlayer");
        /** start the thread */
        thread.start();
    }

	/**
     * Stops then destroys the thread.
     */
    public void stop()
    {
        /** if thread is alive */
        if (thread != null)
        {
            /** destroy it */
            thread.interrupt();
        }
        /** set it to null */
        thread = null;
    }

	/**
     * Thread method.
     */
    public void run() {
		/** iterate */
        do
        {
            /** if the sound object exists */
            if( loadSound(sounds.elementAt(num)) == true )
            {
                /** play the sound */
                playSound();
            }
        }
        /** while loop is on and the thread isn't killed */
        while (loop && thread != null);

        /** if the thread isn't dead */
        if (thread != null)
        {
            /** kill it */
            startB.doClick();
        }
        /** reste everything */
        thread = null;
        currentName = null;
        currentSound = null;
    }

	/**
     * Set pan.
     */
    public void setPan() {
        /** default value of 0: pan left / right evenly */
        int value = 0;
        /** if the sound object is a clip */
        if (currentSound instanceof Clip)
        {
            try
            {
                /** get the clip */
                Clip clip = (Clip) currentSound;
                /** get the current pane value */
                FloatControl panControl =
                (FloatControl) clip.getControl(FloatControl.Type.PAN);
                /** set the pan value */
                panControl.setValue(value/100.0f);
            }
            catch (Exception ex)
            {
            }
        }
        /** if the sound object is a sequence */
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            /** iterate through all the channels */
            for (int i = 0; i < channels.length; i++)
            {
                /** and set the pan values */
                channels[i].controlChange(10,
                (int)(((double)value + 100.0) / 200.0 *  127.0));
            }
        }
    }

	/**
     * Set gain (volumn).
     * @param value volumn value
     */
    public void setGain(double value)
    {
        /** if current sound object is a clip */
        if (currentSound instanceof Clip)
        {
            try
            {
                 /** get the clip */
                Clip clip = (Clip) currentSound;
                /** get the current gain */
                FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                /** create and calculate the gain value */
                float dB = (float)
                (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
                /** set the gain value */
                gainControl.setValue(dB);
            }
            catch (Exception ex)
            {
            }
        }
        /** if current sound object is a sequence */
        else if (currentSound instanceof Sequence ||
        currentSound instanceof BufferedInputStream)
        {
            /** iterate through all the channels */
            for (int i = 0; i < channels.length; i++)
            {
                /** set gain */
                channels[i].controlChange(7, (int)(value * 127.0));
            }
        }
    }

	/**
     * Set gain to 0.
     */
    public void mute()
    {
        volumn = 0;
        setGain(volumn);
        /** pause the sound for a mili second */
        bump = true;
    }

	/**
     * Set gainto 100.
     */
    public void unmute()
    {
        volumn = 100;
        setGain(volumn);
        /** pause the sound for a mili second */
        bump = true;
    }

	/**
     * Change to different sound.
     * @param n new sound offset
     * @param l loop or not
     */
    public void change(int n, boolean l)
    {
        paused = false;
        /** change text of pause button */
        pauseB.setText("Pause");
        /** get loop and offset */
        loop = l;
        num = n;
        /** pause the sound for a mili second */
        bump = true;
        /** if no sound is playing */
        if (startB.getText().equals("Start"))
            /** play the sound */
            startB.doClick();
    }

	/**
     * Play the current sound.
     */
    public void controlPlay()
    {
        startB.doClick();
    }

	 /**
     * Stop the current sound.
     */
    public void controlStop()
    {
        startB.doClick();
    }

	/**
     * Play the previous sound.
     */
    public void controlBack()
    {
        prevB.doClick();
    }

	/**
     * Play the next sound.
     */
    public void controlNext()
    {
        nextB.doClick();
    }

	/**
     * Set buttons states.
     * @param state enabl or disable
     */
    public void setComponentsEnabled(boolean state)
    {
        pauseB.setEnabled(state);
        prevB.setEnabled(state);
        nextB.setEnabled(state);
    }

	/**
     * @return whether something is playing or not
     */
     public boolean isPlaying() {
        return (startB.getText().equals("Start") ? false : true);
     }

	/**
     * Action listener.
     * @param e action event
     */
    public void actionPerformed(ActionEvent e)
    {
        /** get source of event */
        JButton button = (JButton) e.getSource();
        /** if the start button is clicked */
        if (button.getText().equals("Start"))
        {
            /** not paused */
            paused = false;
            /** if offset is out of range than set it to 0 */
            num = num == -1 ? 0 : num;
            /** start playing */
            start();
            /** change button text to "Stop" */
            button.setText("Stop");
            /** change buttons states */
            setComponentsEnabled(true);
        }
        /** if the stop button is clicked */
        else if (button.getText().equals("Stop"))
        {
            /** not paused */
            paused = false;
            /** stop playing */
            stop();
            /** change texts of buttons */
            button.setText("Start");
            pauseB.setText("Pause");
            /** change buttons states */
            setComponentsEnabled(false);
			
        }
        /** if the pause button is clicked */
        else if (button.getText().equals("Pause"))
        {
            /** paused */
            paused = true;
            /** if the sound object is a clip */
            if (currentSound instanceof Clip)
            {
                /** stop the clip */
                ((Clip) currentSound).stop();
            }
            /** if the sound object is a sequence */
            else if (currentSound instanceof Sequence ||
            currentSound instanceof BufferedInputStream)
            {
                /** stop the sequence */
                sequencer.stop();
            }
            /** change the button text */
            pauseB.setText("Resume");
        }
        else if (button.getText().equals("Resume"))
        {
            /** not paused anymore */
            paused = false;
            /** if sound is a clip */
            if (currentSound instanceof Clip)
            {
                /** start the clip */
                ((Clip) currentSound).start();
            }
            /** if sound is a sequence */
            else if (currentSound instanceof Sequence ||
            currentSound instanceof BufferedInputStream)
            {
                /** start the sequence */
                sequencer.start();
            }
            /** change the button text */
            pauseB.setText("Pause");
        }
        /** if the back button is clicked */
        else if (button.getText().equals("<<"))
        {
            paused = false;
            /** change button text */
            pauseB.setText("Pause");
            /** go to previous sound */
            num = num-1 < 0 ? sounds.size()-1 : num-2;
            /** change sound */
            bump = true;
        }
        /** if the next button is clicked */
        else if (button.getText().equals(">>"))
        {
            paused = false;
            /** change button text */
            pauseB.setText("Pause");
            /** go to next sound */
            num = num+1 == sounds.size() ? -1 : num;
            /** change sound */
            bump = true;
        }
    }
}