public class BaseClient {
    private final int id;
    private final String ip;
    private final String nickname;

    public BaseClient(int id, String ip, String nickname) {
        this.id = id;
        this.ip = ip;
        this.nickname = nickname;
    }

    public int getId(){return this.id;}
    public String getIp(){return this.ip;}
    public String getNickname(){return this.nickname;}
}
