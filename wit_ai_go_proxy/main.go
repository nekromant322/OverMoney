package main

import (
	"bytes"
	"context"
	"encoding/binary"
	"encoding/json"
	"errors"
	"fmt"
	"gopkg.in/hraban/opus.v2"
	"io"
	"io/ioutil"
	"net"
	"net/http"
	"os"
	"strings"
)

const keyServerAddr = "serverAddr"

type RecognizerRequestDTO struct {
	Id           string `json:"id"`
	VoiceMessage []byte `json:"voiceMessage"`
}

func main() {

	mux := http.NewServeMux()
	mux.HandleFunc("/actuator/health", getStatus)
	mux.HandleFunc("/recognizer", postRecognize)

	ctx := context.Background()
	server := &http.Server{
		Addr:    ":8080",
		Handler: mux,
		BaseContext: func(l net.Listener) context.Context {
			ctx = context.WithValue(ctx, keyServerAddr, l.Addr().String())
			return ctx
		},
	}

	err := server.ListenAndServe()
	if errors.Is(err, http.ErrServerClosed) {
		fmt.Printf("server closed\n")
	} else if err != nil {
		fmt.Printf("error listening for server: %s\n", err)
	}

}

func getAudioRawBuffer(fileBody []byte) (*bytes.Buffer, error) {
	channels := 1
	s, err := opus.NewStream(bytes.NewReader(fileBody))
	if err != nil {
		return nil, err
	}
	defer s.Close()

	audioRawBuffer := new(bytes.Buffer)
	pcmbuf := make([]int16, 16384)
	for {
		n, err := s.Read(pcmbuf)
		if err == io.EOF {
			break
		} else if err != nil {
			return nil, err
		}
		pcm := pcmbuf[:n*channels]
		err = binary.Write(audioRawBuffer, binary.LittleEndian, pcm)
		if err != nil {
			return nil, err
		}
	}
	return audioRawBuffer, nil
}

func sendPostRequest(fileBody []byte) (string, error) {
	audioRawBuffer, err := getAudioRawBuffer(fileBody)
	if err != nil {
		return "", err
	}

	url := "https://api.wit.ai/dictation"
	client := &http.Client{}
	request, err := http.NewRequest("POST", url, audioRawBuffer)
	if err != nil {
		return "", err
	}

	request.Header.Set("Authorization", os.Getenv("WIT_AI_TOKEN"))
	request.Header.Set("Content-Type", "audio/raw")
	request.Header.Set("Content-Type", "audio/raw;encoding=signed-integer;bits=16;rate=48000;endian=little")

	requestResult, err := client.Do(request)
	if err != nil {
		return "", err
	}

	body, err := ioutil.ReadAll(requestResult.Body)
	if err != nil {
		return "", err
	}

	return string(body), nil
}

func getStatus(w http.ResponseWriter, r *http.Request) {
	statusUP := map[string]interface{}{
		"status": "UP",
	}
	jsonStatusUp, err := json.Marshal(statusUP)
	if err != nil {
		return
	}
	io.WriteString(w, string(jsonStatusUp))
}

func postRecognize(w http.ResponseWriter, r *http.Request) {

	rBody, err1 := ioutil.ReadAll(r.Body)
	if err1 != nil {
		return
	}
	var request RecognizerRequestDTO
	err2 := json.Unmarshal(rBody, &request)
	if err2 != nil {
		return
	}

	byteArray := request.VoiceMessage

	textFromWitAiResponse, _ := sendPostRequest(byteArray)

	msg := string(textFromWitAiResponse)
	var data []map[string]interface{}
	msg = "[" + strings.ReplaceAll(msg, "}\r\n{", "},{") + "]"
	err := json.Unmarshal([]byte(msg), &data)
	var sb strings.Builder
	for _, datum := range data {
		if isFinal, ok := datum["is_final"]; ok {
			isFinalBool, _ := isFinal.(bool)
			if isFinalBool {
				t, _ := datum["text"].(string)
				sb.WriteString(t)
			}
		}
	}
	text := sb.String()

	recognizedText := map[string]interface{}{
		"id":   request.Id,
		"text": text,
	}
	jsonRecognizedText, err := json.Marshal(recognizedText)
	if err != nil {
		return
	}
	io.WriteString(w, string(jsonRecognizedText))
}
