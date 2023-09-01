import json


class ServerLobby:
    name = 'server'
    lobby_id = 0
    manager_client_ip = '0.0.0.0'
    max_players = 0
    players_inside = 1

    def __init__(self, name, lobby_id, manager_client_ip, max_players, players_inside):
        self.name = name
        self.lobby_id = lobby_id
        self.manager_client_ip = manager_client_ip
        self.max_players = max_players
        self.players_inside = players_inside # Needed otherwise to_json() will not send the players_inside value

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=None)

    def add_players_inside(self):
        if self.players_inside == self.max_players:
            raise ValueError("players inside lobby can't be more than max players")
        else:
            self.players_inside += 1

    def remove_players_inside(self):
        if self.players_inside == 0:
            raise ValueError("players inside can't be negative")
        else:
            self.players_inside -= 1
