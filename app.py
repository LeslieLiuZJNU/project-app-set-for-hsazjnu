import http.client
from urllib import request as urllib_request
from flask import Flask
from flask import render_template
from flask import request
import json

app = Flask(__name__)


def find_student_execute(name):
    httpClient = None
    try:
        httpClient = http.client.HTTPConnection('10.1.47.99:8000')
        httpClient.request('GET', '/dataservice/api/1/163?full_name=' + urllib_request.pathname2url(name))
        response = httpClient.getresponse()
        json_string = response.read().decode()
        json_object = json.loads(json_string)
        student_json_list = json_object['data']['rows']
        return student_json_list
    except Exception as e:
        return e
    finally:
        if httpClient:
            httpClient.close()


@app.route('/FindStudentSubmit')
def find_student_submit():
    return render_template('find_student_submit.html')


@app.route('/FindStudentResult', methods=['POST'])
def find_student_result():
    student_name = request.form.get("student_name")
    student_json_list = find_student_execute(student_name)
    if len(student_json_list) != 0:
        return render_template('find_student_result.html', list=student_json_list)
    else:
        return '未找到此人'


if __name__ == '__main__':
    app.run()
