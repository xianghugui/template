# -*- coding:utf-8 -*-

from compare import *
from scipy import misc
import tensorflow as tf
import numpy as np
import sys
import os
import copy
import argparse
import facenet
import align.detect_face
import json
import operator
import mytest


def main(args):
    fr = mytest.face_reconition()
    images = fr.load_dected_align_data(args.margin,
                                       args.image_size,
                                       args.detectedModel,
                                       args.num_epoch,
                                       args.image_path)

    with tf.Graph().as_default():
        with tf.Session() as sess:
            facenet.load_model(args.facenetModel)

            # Get input and output tensors
            images_placeholder = tf.get_default_graph().get_tensor_by_name("input:0")
            embeddings = tf.get_default_graph().get_tensor_by_name("embeddings:0")
            phase_train_placeholder = tf.get_default_graph().get_tensor_by_name("phase_train:0")

            # Run forward pass to calculate embeddings
            feed_dict = {images_placeholder: images, phase_train_placeholder: False}
            emb = sess.run(embeddings, feed_dict=feed_dict)
            with open(args.face_json, 'r') as load_json:
                load_dict = json.load(load_json)

            for j in range(len(images)):
                #similar_people = 0
                similar_list = {}
                print('the pic has %d people, Number %d people:' % (len(images), j))
                for k,b in load_dict.items():
                    dist = np.sqrt(np.sum(np.square(np.subtract(b, emb[j, :]))))
                    similar_list[k] = dist

                    #if dist > 0:
                        #similar_people = dist
                        #people_name = k
                #if similar_people :
                    #print("Is %s , and similar_score is %1.4f" % (people_name, similar_people))
                #else:
                    #print('I do not know %d(similar_score is : %1.4f), he is not in dataset that contain %d of people' % (j, similar_people, len(load_dict)))
                #f=zip(similar_list.values(), similar_list.keys())
                similar_list = sorted(similar_list.items(), key=operator.itemgetter(1), reverse=False)
                for a in range(1):#前三名
                    t, y = similar_list[a]
                    print("Is %s, the core:  %1.4f"%(t, y))
    #return t


def parse_arguments(argv):
    parser = argparse.ArgumentParser()

    parser.add_argument('face_json', type=str,
                        help='the json file path that contain features of face')
    parser.add_argument('--image_size', type=int,
                        help='Image size (height, width) in pixels.', default=160)
    parser.add_argument('--margin', type=int,
                        help='Margin for the crop around the bounding box (height, width) in pixels.', default=44)
    parser.add_argument('--detectedModel', type=str, nargs='+', default=['../data/MTCNN_model/PNet_landmark/PNet',
                                                                      '../data/MTCNN_model/RNet_landmark/RNet',
                                                                      '../data/MTCNN_model/ONet_landmark/ONet'])
    parser.add_argument('--facenetModel', type=str, default='/home/zxwl/FaceNet/20170512-110547')
    parser.add_argument('--image_path', type=str, default='lala/02.jpg')
    parser.add_argument('--num_epoch', type=int, nargs='+', default=[30, 16, 8])
    return parser.parse_args(argv)

if __name__ == '__main__':
    main(parse_arguments(sys.argv[1:]))