package io.github.buraconcio.Utils;

public class Auxiliaries{

    //limpar o terminal para debug
    public final static void cls(){
        try{
            final String os = System.getProperty("os.name");

            if(os.contains("Windows")){

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            }else{

                System.out.print("\033[H\033[2J");
                System.out.flush();

            }
        }catch(final Exception e){
            System.out.println("erro ao limpar terminal" + e.getMessage());
        }
    }

}
