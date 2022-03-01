class ServerLobby:
    name = ''
    manager_client_ip = ''
    max_players = ''
    players_inside = ''
    id_server_lobby_manager = ''

    def __init__(self, name, manager_client_ip, max_players, players_inside, id_server_lobby_manager):
        self.name = name
        self.manager_client_ip = manager_client_ip
        self.max_players = max_players
        self.players_inside = players_inside
        self.id_server_lobby_manager = id_server_lobby_manager
