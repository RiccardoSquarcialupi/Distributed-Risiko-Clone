FROM python:3.7-alpine
WORKDIR .
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
RUN pip install flask
COPY /server/WebServer/src .
EXPOSE 5000
CMD ["flask", "run"]