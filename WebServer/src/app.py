from flask import *
from ServerLobby import ServerLobby

app = Flask(__name__)


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
        if lobby.max_players == max_players:
            result.append(lobby.toJSON())
    return jsonify(result), 200


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
    if request.form.get('name') is not None and request.form.get('max_players') is not None:
        add_lobby_to_dict(request.form['name'], request.remote_addr, request.form['max_players'])
        return "", 200
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

    if request.form.get('new_manager_client_ip') is None:
        return "New manager not set", 400

    temporary_dict_of_lobbies[lobby_id].manager_client_ip = request.form.get('new_manager_client_ip')
    return 'Hello World!'


@app.route('/server/lobby', methods=['GET'])
def connect_to_lobby():
    return 'Hello World!'


def add_lobby_to_dict(name, manager_ip, max_players):
    global count
    global temporary_dict_of_lobbies
    temporary_dict_of_lobbies[count] = ServerLobby(name, count, manager_ip, max_players)
    count += 1


if __name__ == '__main__':
    temporary_dict_of_lobbies = {}
    count = 0
    app.run()
