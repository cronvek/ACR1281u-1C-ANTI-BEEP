import java.io.Console;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class PCSC {

    private byte[] atr = null;
    private String protocol = null;
    private byte[] historical = null;

    public CardTerminal selectCardTerminal() {
        try {
            // show the list of available terminals
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            ListIterator<CardTerminal> terminalsIterator = terminals
                    .listIterator();
            CardTerminal terminal = null;
            CardTerminal defaultTerminal = null;
            if (terminals.size() > 1) {
                int i = 1;
                while (terminalsIterator.hasNext()) {                	
                	terminal = terminalsIterator.next();
                    //System.out.print("[" + terminal.getName() + "]");
                    if (terminal.getName().equals("ACS ACR1281 1S Dual Reader ICC 0"))
                    {
                    	//we select that one, else we return a list of options.
                    	//But we should put a prompt telling them to put a card in.
                    	System.out.println("Please insert a card into " + terminal.getName() + " and press enter.");
                    	System.in.read();                 	                    	
                    	System.out.println("Selected: " + terminal.getName());
                    	return terminal;
                    }
                    System.out.print("[" + i + "] - " + terminal
                            + ", card present: " + terminal.isCardPresent());
                    if (i == 1) {
                        defaultTerminal = terminal;
                        System.out.println(" [default terminal]");
                    } else {
                        System.out.println();
                    }
                    i++;
                    
                }
                Scanner in = new Scanner(System.in);
                try {
                    int option = in.nextInt();
                    terminal = terminals.get(option - 1);
                } catch (Exception e2) {
                    // System.err.println("Wrong value, selecting default terminal!");
                    terminal = defaultTerminal;

                }
                System.out.println("Selected: " + terminal.getName());
                // Console console = System.console();
                return terminal;
            }

        } catch (Exception e) {
            System.err.println("Error occured:");
            e.printStackTrace();
        }
        return null;
    }

    public String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public Card establishConnection(CardTerminal ct) {
        this.atr = null;
        this.historical = null;
        this.protocol = null;

        String p = "*";

        p = "*";
        System.out.println("Selected: " + p);

        Card card = null;
        try {
            card = ct.connect(p);
        } catch (CardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        ATR atr = card.getATR();
        System.out.println("Connected:");
        System.out.println(" - ATR:  " + byteArrayToHexString(atr.getBytes()));
        System.out.println(" - Historical: "
                + byteArrayToHexString(atr.getHistoricalBytes()));
        System.out.println(" - Protocol: " + card.getProtocol());

        this.atr = atr.getBytes();
        this.historical = atr.getHistoricalBytes();
        this.protocol = card.getProtocol();

        return card;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    	System.out.println("Anti-beep 5000 now with the ability to switch on/off beeps. By Liz Mackie. Comes with no guarantees!");
    	
        PCSC pcsc = new PCSC();
        CardTerminal ct = pcsc.selectCardTerminal();
        Card c = null;    
        if (ct != null) {
            c = pcsc.establishConnection(ct);
            CardChannel cc = c.getBasicChannel();

            try{
            	
           int option = 1;
	//Card.transmitControlCommand()
            	System.out.println("1 to Turn beeps off, 2 to Turn Beeps on:");
                Scanner in = new Scanner(System.in);
                try {
                    option = in.nextInt();

                } catch (Exception e2) {
                	
                	System.out.println("Turning beeps off for that.:");
                }
            	
            	if (option == 1)
            	{
	            	byte[] cmd = {(byte)0xe0,0,0,0x21,0x01, (byte) 0x83};
	            	byte[] answer = c.transmitControlCommand(0x003136B0, cmd);
	            	 StringBuilder sb = new StringBuilder();
	            	 for (byte b : answer) {
	            	        sb.append(String.format("%02X ", b));
	            	    }
	            	    System.out.println(sb.toString());
            	}
            	else
            	{
	            	byte[] cmd = {(byte)0xe0,0,0,0x21,0x01, (byte) 0xF3};
	            	byte[] answer = c.transmitControlCommand(0x003136B0, cmd);
	            	 StringBuilder sb = new StringBuilder();
	            	 for (byte b : answer) {
	            	        sb.append(String.format("%02X ", b));
	            	    }
	            	    System.out.println(sb.toString());
            	}
           }    
              

            catch (CardException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}