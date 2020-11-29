FROM python:3.8.6-alpine

RUN pip install pipenv
COPY Pipfile* /tmp/
RUN cd /tmp && pipenv lock --requirements > requirements.txt
RUN pip install -r /tmp/requirements.txt
RUN mkdir /opt/data/
COPY data/ipma_humidade_media_ar_2018.csv /opt/data/ipma_humidade_media_ar_2018.csv
COPY data/ipma_precipitacao_total_2018.csv /opt/data/ipma_precipitacao_total_2018.csv
COPY data/ipma_radiacao_solar_2018.csv /opt/data/ipma_radiacao_solar_2018.csv
COPY data/ipma_radiacao_solar_2018.csv /opt/data/ipma_radiacao_solar_2018.csv
COPY data/ipma_temperatura_media_ar_2018.csv /opt/data/ipma_temperatura_media_ar_2018.csv
COPY data/ipma_vento_intensidade_media_2018.csv /opt/data/ipma_vento_intensidade_media_2018.csv
COPY data/ipma_vento_rumo_media_2018.csv /opt/data/ipma_vento_rumo_media_2018.csv

COPY main.py /opt/main.py

ENTRYPOINT ["python", "/opt/main.py", "-dp", "/opt/data/"]