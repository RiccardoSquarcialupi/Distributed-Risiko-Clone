class ServerLobby:
    name = ''
    manager_client_ip = ''
    max_players = ''
    players_inside = 0

    def __init__(self, name, manager_client_ip, max_players):
        self.name = name
        self.manager_client_ip = manager_client_ip
        self.max_players = max_players
