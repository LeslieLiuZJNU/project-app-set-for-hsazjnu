import http.client
from urllib import request as urllib_request
from flask import Flask
from flask import render_template
from flask import request
import json

app = Flask(__name__)


def find_student_execute(name):
    http_client = None
    try:
        http_client = http.client.HTTPConnection('10.1.47.99:8000')
        http_client.request('GET', '/dataservice/api/1/163?full_name=' + urllib_request.pathname2url(name))
        response = http_client.getresponse()
        json_string = response.read().decode()
        json_object = json.loads(json_string)
        student_json_list = json_object['data']['rows']
        return student_json_list
    except Exception as e:
        return e
    finally:
        if http_client:
            http_client.close()


@app.route('/FindStudentSubmit')
def find_student_submit():
    return render_template('find_student_submit.html')


@app.route('/FindStudentResult', methods=['POST'])
def find_student_result():
    student_name = request.form.get('student_name')
    if student_name != '':
        student_json_list = find_student_execute(student_name)
        if len(student_json_list) != 0:
            return render_template('find_student_result.html', list=student_json_list)
        else:
            return '未找到此人'
    else:
        return render_template('find_student_submit.html', msg='请输入姓名')


@app.route('/DataDisplay')
def data_display():
    return render_template('data_display.html')


if __name__ == '__main__':
    app.run(debug=True)


#f=open()
#json.loads()[data]

#@app.route('/Submit')
#def sub():


#@app.route('/Display')
#def dis():