FROM python:3.9.1-alpine

RUN pip install pipenv
COPY Pipfile* /tmp/
RUN cd /tmp && pipenv lock --requirements > requirements.txt
RUN pip install -r /tmp/requirements.txt

COPY main.py /opt/main.py

ENTRYPOINT ["python", "/opt/main.py"]