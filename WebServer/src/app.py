from flask import *
from ServerLobby import ServerLobby

app = Flask(__name__)


@app.route('/server/lobbies/<maxPlayers>', methods=['GET'])
def lobbies_matching_client_filter(maxPlayers):
    return 'Hello World!' + str(maxPlayers)


@app.route('/server/lobby/<id>', methods=['DELETE'])
def match_started_lobby_deleted(id):
    return 'Hello World!'


@app.route('/server/lobbies', methods=['POST'])
def create_new_lobby():
    if request.form.get('name') is not None and request.form.get('max_players') is not None:
        add_lobby_to_dict(request.form['name'], request.remote_addr, request.form['max_players'])
    else:
        return "Arguments not found", 401


@app.route('/server/lobby/<id>/numberOfPlayer', methods=['PUT'])
def exit_lobby_decrement_number_of_players(id):
    return 'Hello World!'


@app.route('/server/lobby/<id>/managerClientIp', methods=['PUT'])
def update_manager_client_info(id):
    return 'Hello World!'


@app.route('/server/lobby', methods=['GET'])
def connect_to_lobby():
    return 'Hello World!'


def add_lobby_to_dict(name, manager_ip, max_players):
    global count
    global temporary_dict_of_lobbies
    temporary_dict_of_lobbies[count] = ServerLobby(name, manager_ip, max_players)
    count += 1


if __name__ == '__main__':
    temporary_dict_of_lobbies = {}
    count = 0
    app.run()

