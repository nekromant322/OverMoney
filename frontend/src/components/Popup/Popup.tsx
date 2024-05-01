import React, { FC } from 'react';
import { Button, Modal } from 'react-bootstrap';

interface PopupProps {
  title: string,
  titleButton: string,
  show: boolean,
  onSubmit: React.MouseEventHandler<HTMLButtonElement>,
  handleClose: () => void,
  children: React.ReactNode
}

const Popup: FC<PopupProps> = ({children, title, titleButton, show, onSubmit, handleClose}) => {
  return (
    <Modal  show={show} onHide={handleClose}>
      <Modal.Header data-bs-theme="dark" closeButton>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {children}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={onSubmit}>
          {titleButton}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default Popup;