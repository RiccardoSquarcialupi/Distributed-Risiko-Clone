from flask import *
from ServerLobby import ServerLobby

app = Flask(__name__)
temporary_dict_of_lobbies = {}
count = 0


@app.route('/server/lobbies/<max_players>', methods=['GET'])
def lobbies_matching_client_filter(max_players):
    if max_players is None:
        return "Argument not set", 404

    if not str(max_players).isnumeric():
        return "Number of player is not a number", 404

    if int(max_players) < 3 or int(max_players) > 6:
        return "Number of player is inconsistent", 404

    global temporary_dict_of_lobbies
    result = []
    for lobby in temporary_dict_of_lobbies.values():
        if int(lobby.max_players) == int(max_players):
            result.append(lobby.to_json())
    return result, 200


@app.route('/server/lobby/<lobby_id>', methods=['DELETE'])
def match_started_lobby_deleted(lobby_id):
    if not str(lobby_id).isnumeric():
        return "Lobby id is not a number", 404

    lobby_id = int(lobby_id)
    global temporary_dict_of_lobbies

    if lobby_id not in temporary_dict_of_lobbies:
        return "Lobby not found", 404
    temporary_dict_of_lobbies.pop(lobby_id)
    return "", 200


@app.route('/server/lobbies', methods=['POST'])
def create_new_lobby():
    global count
    id = count
    if request.get_json().get('name') is not None and request.get_json().get('max_players') is not None:
        add_lobby_to_dict(request.get_json()['name'], request.remote_addr, request.get_json()['max_players'],1)
        return jsonify(id), 200
    else:
        return "Arguments not found", 401


@app.route('/server/lobby/<lobby_id>/numberOfPlayer', methods=['DELETE'])
def exit_lobby_decrement_number_of_players(lobby_id):
    if not str(lobby_id).isnumeric():
        return "Lobby id is not a number", 404

    lobby_id = int(lobby_id)
    global temporary_dict_of_lobbies

    if lobby_id not in temporary_dict_of_lobbies:
        return "Lobby not found", 404

    temporary_dict_of_lobbies[lobby_id].players_inside -= 1
    return '', 200


@app.route('/server/lobby/<lobby_id>/managerClientIp', methods=['PUT'])
def update_manager_client_info(lobby_id):
    if not str(lobby_id).isnumeric():
        return "Lobby id is not a number", 404

    lobby_id = int(lobby_id)
    global temporary_dict_of_lobbies

    if lobby_id not in temporary_dict_of_lobbies:
        return "Lobby not found", 404

    if request.get_json().get('new_manager_client_ip') is None:
        return "New manager not set", 400

    temporary_dict_of_lobbies[lobby_id].manager_client_ip = request.get_json().get('new_manager_client_ip')
    return '', 200


@app.route('/server/lobby/<lobby_id>', methods=['PUT'])
def connect_to_lobby(lobby_id):
    if not str(lobby_id).isnumeric():
        return "Lobby id is not a number", 404

    lobby_id = int(lobby_id)
    global temporary_dict_of_lobbies

    if lobby_id not in temporary_dict_of_lobbies:
        return "Lobby not found", 404

    temporary_dict_of_lobbies[lobby_id].players_inside += 1

    return '', 200


def add_lobby_to_dict(name, manager_ip, max_players, players_inside):
    global count
    global temporary_dict_of_lobbies
    temporary_dict_of_lobbies[count] = ServerLobby(name, count, manager_ip, max_players, players_inside)
    count += 1


if __name__ == '__main__':
    app.run()
