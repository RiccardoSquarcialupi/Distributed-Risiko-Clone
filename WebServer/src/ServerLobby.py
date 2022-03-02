import json


class ServerLobby:
    name = ''
    manager_client_ip = ''
    max_players = ''
    players_inside = 0

    def __init__(self, name, manager_client_ip, max_players):
        self.name = name
        self.manager_client_ip = manager_client_ip
        self.max_players = max_players

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
