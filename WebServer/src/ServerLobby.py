import json


class ServerLobby:
    name = ''
    lobby_id = 0
    manager_client_ip = ''
    max_players = 0
    players_inside = 1

    def __init__(self, name, lobby_id, manager_client_ip, max_players):
        self.name = name
        self.lobby_id = lobby_id
        self.manager_client_ip = manager_client_ip
        self.max_players = max_players

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
