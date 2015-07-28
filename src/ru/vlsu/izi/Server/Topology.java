package ru.vlsu.izi.Server;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by user on 08.07.15.
 */
public class Topology {

    ArrayList<HashSet<Integer>> table = new ArrayList<>();
    ArrayList<Integer> params = new ArrayList<>();
    int size;

    public Topology(int size) {
        this.size = size;

        while (table.size() < size) {
            table.add(new HashSet<>());
        }

    }

    public HashSet<Integer> getNeighbourList(int i) {
        return table.get(i);
    }


    public int size() {
        return size;
    }

    void link(int i, int j) {
        table.get(i).add(j);
        table.get(j).add(i);
    }

    public boolean isLinked(int i, int j) {
        return table.get(i).contains(j);
    }

    public static Topology fromFile(String file) throws FileNotFoundException {
        Topology topology;

        FileInputStream fstream = new FileInputStream(file);

        try(
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        ) {
            String strLine = br.readLine();

            // Read params
            ArrayList<Integer> params = new ArrayList<>();
            String[] temp = strLine.split(" ");
            for (String element : temp) {
                int tempint =  Integer.parseInt(element);
                params.add(tempint);
            }

            topology = new Topology(params.size());
            topology.params = params;

            // Read topology
            int line = 0;
            while ((strLine = br.readLine()) != null) {
                ArrayList<String> temp1 = new ArrayList<String>(Arrays.asList(strLine
                        .split(" ")));
                ArrayList<Integer> temp2 = new ArrayList<Integer>();
                for (String element : temp1) {
                    int tempint = Integer.parseInt(element);
                    topology.link(line, tempint);
                }

                line++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return topology;

    }
}
