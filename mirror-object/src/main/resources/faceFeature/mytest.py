#coding:utf-8
from __future__ import print_function#声明本文中用到的print为python3版本
from __future__ import absolute_import
from __future__ import division

import sys
sys.path.append('..')
from Detection.MtcnnDetector import MtcnnDetector
from Detection.detector import Detector
from Detection.fcn_detector import FcnDetector
from train_models.mtcnn_model import P_Net, R_Net, O_Net
from prepare_data.loader import TestLoader
import cv2
import os
import numpy as np
import argparse
from scipy import misc
import tensorflow as tf
import facenet
import json

def main(args):
    fc = face_reconition()
    #save_path = './feature.json'
    fc.images_to_vectors(args.image_path, args.save_path, args.facenetModel, args.margin, args.image_size, args.detecedModel, args.num_epoch)
    #images = fc.load_dected_align_data(args.margin, args.image_size, args.detecedModel, args.num_epoch, args.image_path)

    #if images is None:
        #print('No Face was detected')
        #return None


class face_reconition:
    def __init__(self):
        pass
    #读取文件下的图片集
    def get_image_paths(self, inpath):
        paths = []
        for file in os.listdir(inpath):
            if os.path.isfile(os.path.join(inpath, file)):
                if file.lower().endswith(('.png', '.jpg', '.jpeg')) is False:
                    continue

                paths.append(os.path.join(inpath, file))

        return (paths)

    #图片的特征值保存成.json文件
    def images_to_vectors(self, inpath, outjson_path, facenetModel,margin, image_size, detectedModel, epoch):
        results = dict()

        with tf.device('/gpu:0'):
            with tf.Graph().as_default():
                with tf.Session() as sess:
                    facenet.load_model(facenetModel)
                    # Get input and output tensors
                    images_placeholder = tf.get_default_graph().get_tensor_by_name("input:0")
                    embeddings = tf.get_default_graph().get_tensor_by_name("embeddings:0")
                    phase_train_placeholder = tf.get_default_graph().get_tensor_by_name("phase_train:0")

                    image_paths = self.get_image_paths(inpath)

                    #uid_acc = 0
                    #num_face = 0

                    #t0 = time.time()
                    for image_path in image_paths:
                        #img = misc.imread(os.path.expanduser(image_path), mode='RGB')
                        images = self.load_dected_align_data(margin, image_size, detectedModel, epoch, image_path)
                        #num_face += images.shape[0]
                        # 判断是否检测出人脸 检测不出 就跳出此循环
                        if images.shape[0] == 0: continue

                        feed_dict = {images_placeholder: images, phase_train_placeholder: False}
                        emb_array = sess.run(embeddings, feed_dict=feed_dict)
                        filepath, filename = os.path.split(image_path)
                        filename, file_extension = os.path.splitext(filename)

                        filename_base, file_extension = os.path.splitext(image_path)
                        for j in range(0, len(emb_array)):
                            # results[filename_base + "_" + str(j)] = emb_array[j].tolist()
                            #uid_acc += 1
                            results[filename] = emb_array[j].tolist()  # 文件名作为json字典里的键

                    #if not num_face is 0:
                        #print("total: %d   Detected, cost:  %f" % (num_face, (time.time() - t0)))
                    # """

                sess.close()
        # All done, save for later!
        json.dump(results, open(outjson_path, "w"))
        # 返回图像中所有人脸的向量

    def load_dected_align_data(selt, margin, image_size, model, epoch, image_file):
        thresh = [0.6, 0.7, 0.8]
        min_face_size = 20
        stride = 2
        slide_window = False
        shuffle = False
        detectors = [None, None, None]
        batch_size = [2048, 256, 16]
        img_list = []
        model_path = ['%s-%s' % (x, y) for x, y in zip(model, epoch)]
        # load model
        if slide_window:
            PNet = Detector(P_Net, 12, batch_size[0], model_path[0])
        else:
            PNet = FcnDetector(P_Net, model_path[0])
        detectors[0] = PNet
        RNet = Detector(R_Net, 24, batch_size[1], model_path[1])
        detectors[1] = RNet
        ONet = Detector(O_Net, 48, batch_size[2], model_path[2])
        detectors[2] = ONet

        mtcnn_detector = MtcnnDetector(detectors=detectors, min_face_size=min_face_size,
                                       stride=stride, threshold=thresh, slide_window=slide_window)
        gt_imdb = []
        # gt_imdb.append("35_Basketball_Basketball_35_515.jpg")
        # imdb_ = dict()"
        # imdb_['image'] = im_path
        # imdb_['label'] = 5
        #path = 'lala/a'
        #for a in os.listdir(path):
            #gt_imdb.append(os.path.join(path, a))
        gt_imdb.append(image_file)
        test_data = TestLoader(gt_imdb)
        all_boxes, landmarks = mtcnn_detector.detect_face(test_data)
        image = cv2.imread(image_file)
        img_size = image.shape  # 高,宽,通道
        """
        count = 0
        for imagepath in gt_imdb:
            print(imagepath)
            image = cv2.imread(imagepath)
            draw = image.copy()
            img_size = image.shape  #高,宽,通道
            for bbox in all_boxes[count]:
                cv2.putText(draw, str(np.round(bbox[4], 1)), (int(bbox[0]), int(bbox[1])), cv2.FONT_HERSHEY_TRIPLEX, 1,
                            color=(255, 0, 0))
                cv2.rectangle(draw, (int(bbox[0]), int(bbox[1])), (int(bbox[2]), int(bbox[3])), (0, 0, 255))

            for landmark in landmarks[count]:
                for i in range(5):
                    cv2.circle(draw, (int(landmark[2 * i]), int(int(landmark[2 * i + 1]))), 1, (0, 255, 0))

            count = count + 1
            cv2.imwrite("result_landmark/%d.png" % (count), draw)
            cv2.imshow("lala", draw)
            c = cv2.waitKey(0)
            if c is 27:
                cv2.destroyAllWindows()
        """
        all_boxes = np.array(all_boxes)
        for i in range(len(all_boxes[0])):
            det = np.squeeze(all_boxes[0, i, 0:4])
            bb = np.zeros(4, dtype=np.int32)
            bb[0] = np.maximum(det[0] - margin / 2, 0)  # 扩大人脸区域
            bb[1] = np.maximum(det[1] - margin / 2, 0)
            bb[2] = np.minimum(det[2] + margin / 2, img_size[1])
            bb[3] = np.minimum(det[3] + margin / 2, img_size[0])
            cropped = image[bb[1]:bb[3], bb[0]:bb[2], :]
            #cv2.imwrite('./%d.jpg'%i, cropped)
            aligned = misc.imresize(cropped, (image_size, image_size), interp='bilinear')
            prewhitened = selt.prewhiten(aligned)
            img_list.append(prewhitened)
        if img_list.__len__() is 0:
            return None
        images = np.stack(img_list)
        return images

    def prewhiten(self, x):
        mean = np.mean(x)
        std = np.std(x)
        std_adj = np.maximum(std, 1.0/np.sqrt(x.size))
        y = np.multiply(np.subtract(x, mean), 1/std_adj)
        return y

def parse_arguments(argv):
    parser = argparse.ArgumentParser()

    parser.add_argument('image_path', type=str, help='Inout Images for detected and gain features')
    parser.add_argument('save_path', type=str, help='the path of image features')
    parser.add_argument('--detecedModel', type=str, nargs='+', default=['../data/MTCNN_model/PNet_landmark/PNet',
                                                                '../data/MTCNN_model/RNet_landmark/RNet',
                                                                '../data/MTCNN_model/ONet_landmark/ONet'])
    parser.add_argument('--facenetModel', type=str, default='/home/zxwl/FaceNet/20170512-110547')
    parser.add_argument('--num_epoch', type=int, nargs='+', default=[30, 16, 8])
    parser.add_argument('--margin', type=int, default=44)
    parser.add_argument('--image_size', type=int, default=160)
    return parser.parse_args(argv)

if __name__ == '__main__':
    main(parse_arguments(sys.argv[1:]))
