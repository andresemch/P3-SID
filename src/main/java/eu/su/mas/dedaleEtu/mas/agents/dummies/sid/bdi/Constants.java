package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

public class Constants {
    public static String I_AM_REGISTERED = "IAmRegistered";
    public static String ONTOLOGY = "ontology";
    public static String QUERY_SITUATED_AGENT =
                    "PREFIX example: <http://example#> " +
                    "SELECT ?Agent where {" +
                    " ?Agent a example:Agent ."+
                    "}";

    public static String RECEIVE_INITIAL_POS = "ReceiveInitialPos";

    public static String NO_OPEN_NODES_LEFT = "NoOpenNodesLeft";

    public static String OPEN_NODES= "Open Nodes";
    public static String CLOSED_NODES= "Closed_Nodes";
    public static String MAPA= "Map";
    public static String HISTORICAL="historical";
}
